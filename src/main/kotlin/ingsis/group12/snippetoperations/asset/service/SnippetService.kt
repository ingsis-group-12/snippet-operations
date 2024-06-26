package ingsis.group12.snippetoperations.asset.service

import ingsis.group12.snippetoperations.asset.dto.PermissionDTO
import ingsis.group12.snippetoperations.asset.dto.ShareDTO
import ingsis.group12.snippetoperations.asset.dto.SnippetDTO
import ingsis.group12.snippetoperations.asset.dto.UserShareDTO
import ingsis.group12.snippetoperations.asset.input.AssetInput
import ingsis.group12.snippetoperations.asset.input.SnippetInput
import ingsis.group12.snippetoperations.asset.input.SnippetUpdateInput
import ingsis.group12.snippetoperations.asset.model.ComplianceType
import ingsis.group12.snippetoperations.asset.model.Snippet
import ingsis.group12.snippetoperations.asset.repository.SnippetRepository
import ingsis.group12.snippetoperations.bucket.ObjectStoreService
import ingsis.group12.snippetoperations.exception.SnippetCreationError
import ingsis.group12.snippetoperations.exception.SnippetDeleteError
import ingsis.group12.snippetoperations.exception.SnippetNotFoundError
import ingsis.group12.snippetoperations.exception.SnippetShareError
import ingsis.group12.snippetoperations.permission.model.SnippetPermission
import ingsis.group12.snippetoperations.permission.service.PermissionService
import ingsis.group12.snippetoperations.rule.dto.LinterRuleInput
import ingsis.group12.snippetoperations.rule.service.RuleService
import ingsis.group12.snippetoperations.runner.input.LinterInput
import ingsis.group12.snippetoperations.runner.output.LinterOutput
import ingsis.group12.snippetoperations.runner.service.RunnerService
import ingsis.group12.snippetoperations.util.parseLintingRulesToString
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class SnippetService(
    private val snippetRepository: SnippetRepository,
    private val objectStoreService: ObjectStoreService,
    private val permissionService: PermissionService,
    private val runnerService: RunnerService,
    private val linterRuleService: RuleService<LinterRuleInput, LinterOutput>,
) : AssetService {
    override fun createAsset(
        assetInput: AssetInput,
        userId: String,
    ): SnippetDTO {
        val input = assetInput as SnippetInput
        val snippetId = UUID.randomUUID()
        val permissionResponse = permissionService.create(userId, snippetId, PermissionDTO("owner", input.userName))
        if (permissionResponse.statusCode.is2xxSuccessful) {
            val storageResponse = objectStoreService.create(input.content, snippetId)
            if (storageResponse.statusCode.is2xxSuccessful) {
                return saveSnippet(input, snippetId, userId)
            }
        }
        throw SnippetCreationError("Error while creating snippet")
    }

    override fun getAssetById(assetId: UUID): SnippetDTO {
        val result = snippetRepository.findById(assetId)
        if (result.isPresent) {
            val snippet = result.get()
            val content = objectStoreService.get(assetId).body!!
            return SnippetDTO(
                assetId,
                snippet.name!!,
                content,
                snippet.language!!,
                snippet.extension!!,
                complianceType = snippet.compliance,
            )
        }
        throw SnippetNotFoundError("Snippet not found")
    }

    override fun getAssets(userId: String): List<SnippetDTO> {
        val permissions = permissionService.getUserPermissionsByUserId(userId).body!!
        return permissions.map { permission ->
            val snippetPermission = permission as SnippetPermission
            val snippet = snippetRepository.findById(snippetPermission.assetId).get()
            val content = objectStoreService.get(snippetPermission.assetId).body!!

            SnippetDTO(
                permission.assetId,
                snippet.name!!,
                content,
                snippet.language!!,
                snippet.extension!!,
                snippetPermission.userName,
                snippetPermission.userId,
                snippet.compliance,
            )
        }
    }

    override fun updateAsset(
        assetId: UUID,
        assetInput: AssetInput,
        userId: String,
    ): SnippetDTO {
        val input = assetInput as SnippetUpdateInput
        val snippetOptional = snippetRepository.findById(assetId)
        if (snippetOptional.isPresent && canUpdate(userId, assetId)) {
            val snippet = snippetOptional.get()
            val lintingRules = linterRuleService.createOrGetRules(userId)
            val lintingRulesToString = parseLintingRulesToString(lintingRules)
            val lintingResult = applyRules(snippet, input.content, lintingRulesToString)
            val snippetCopy = snippet.copy(updatedAt = Date(), compliance = lintingResult)
            snippetRepository.save(snippetCopy)
            objectStoreService.update(input.content, assetId)
            return SnippetDTO(
                assetId,
                input.name,
                input.content,
                snippet.language!!,
                snippet.extension!!,
                complianceType = lintingResult,
            )
        }
        throw SnippetNotFoundError("Snippet not found")
    }

    override fun deleteAssetById(
        assetId: UUID,
        userId: String,
    ): String {
        val result = snippetRepository.findById(assetId)
        if (result.isPresent && isOwner(assetId, userId)) {
            val objectResponse = objectStoreService.delete(assetId)
            val permissionsResponse = permissionService.deletePermissionsByAssetId(assetId)
            if (objectResponse.statusCode.is2xxSuccessful && permissionsResponse.statusCode.is2xxSuccessful) {
                snippetRepository.deleteById(assetId)
                return "Snippet deleted with id $assetId"
            } else {
                throw SnippetDeleteError("Error while deleting snippet from bucket")
            }
        }
        throw SnippetNotFoundError("Snippet not found")
    }

    override fun shareAsset(
        userId: String,
        shareDTO: ShareDTO,
    ) {
        val result = snippetRepository.findById(shareDTO.assetId)
        if (result.isPresent && isOwner(shareDTO.assetId, userId)) {
            permissionService.create(shareDTO.userId, shareDTO.assetId, PermissionDTO("read", shareDTO.userName))
        } else {
            throw SnippetNotFoundError("Snippet not found")
        }
    }

    fun getUsersToShareSnippetWith(
        snippetId: UUID,
        ownerId: String,
    ): List<UserShareDTO> {
        val result = snippetRepository.findById(snippetId)
        if (result.isEmpty) {
            throw SnippetNotFoundError("Snippet not found")
        }
        if (isOwner(snippetId, ownerId)) {
            val permissions = permissionService.getUsersWhoNotHavePermissionWithAsset(snippetId, ownerId).body!!
            return permissions.map { permission ->
                UserShareDTO(permission.userId, permission.userName)
            }
        } else {
            throw SnippetShareError("You are not the owner of the snippet")
        }
    }

    private fun applyRules(
        snippet: Snippet,
        content: String,
        linterRules: String,
    ): ComplianceType {
        val result =
            runnerService.analyze(
                LinterInput(content, snippet.language, linterRules),
            )

        return getSnippetCompliance(result)
    }

    private fun getSnippetCompliance(result: LinterOutput): ComplianceType {
        if (result.errors.isNotBlank()) {
            return if (result.output.contains("ReportFailure")) {
                ComplianceType.NOT_COMPLIANT
            } else {
                ComplianceType.FAILED
            }
        }
        return ComplianceType.COMPLIANT
    }

    private fun saveSnippet(
        snippetInput: SnippetInput,
        snippetId: UUID,
        userId: String,
    ): SnippetDTO {
        val snippetSaved =
            snippetRepository.save(
                Snippet(
                    id = snippetId,
                    name = snippetInput.name,
                    language = snippetInput.language,
                    extension = snippetInput.extension,
                    compliance = ComplianceType.PENDING,
                ),
            )
        return SnippetDTO(
            snippetId,
            snippetInput.name,
            snippetInput.content,
            snippetInput.language,
            snippetInput.extension,
            snippetInput.userName,
            userId = userId,
            complianceType = snippetSaved.compliance,
        )
    }

    private fun isOwner(
        assetId: UUID,
        userId: String,
    ) = permissionService.getUserPermissionByAssetId(assetId, userId).body!!.permission == "owner"

    private fun canUpdate(
        userId: String,
        snippetId: UUID,
    ): Boolean {
        val response = permissionService.getUserPermissionByAssetId(snippetId, userId).body!!
        return response.permission != "read"
    }
}
