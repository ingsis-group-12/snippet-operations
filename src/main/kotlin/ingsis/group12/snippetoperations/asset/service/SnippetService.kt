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
import ingsis.group12.snippetoperations.exception.SnippetPermissionError
import ingsis.group12.snippetoperations.exception.SnippetShareError
import ingsis.group12.snippetoperations.permission.model.SnippetPermission
import ingsis.group12.snippetoperations.permission.service.PermissionService
import ingsis.group12.snippetoperations.rule.dto.LinterRuleInput
import ingsis.group12.snippetoperations.rule.service.RuleService
import ingsis.group12.snippetoperations.runner.input.ExecutorInput
import ingsis.group12.snippetoperations.runner.input.LinterInput
import ingsis.group12.snippetoperations.runner.output.ExecutorOutput
import ingsis.group12.snippetoperations.runner.output.LinterOutput
import ingsis.group12.snippetoperations.runner.service.RunnerService
import ingsis.group12.snippetoperations.util.parseLintingRulesToString
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(SnippetService::class.java)

    override fun createAsset(
        assetInput: AssetInput,
        userId: String,
    ): SnippetDTO {
        logger.info("Creating snippet")
        val input = assetInput as SnippetInput
        val snippetId = UUID.randomUUID()
        logger.info("Creating permission")
        val permissionResponse = permissionService.create(userId, snippetId, PermissionDTO("owner", input.userName))
        if (permissionResponse.statusCode.is2xxSuccessful) {
            logger.info("Creating snippet in bucket")
            val storageResponse = objectStoreService.create(input.content, snippetId)
            if (storageResponse.statusCode.is2xxSuccessful) {
                logger.info("Saving snippet on database")
                return saveSnippet(input, snippetId, userId)
            }
        }
        logger.error("Error while creating snippet")
        throw SnippetCreationError("Error while creating snippet")
    }

    override fun getAssetById(assetId: UUID): SnippetDTO {
        logger.info("Getting snippet by id")
        val result = snippetRepository.findById(assetId)
        if (result.isPresent) {
            logger.info("Snippet was in database")
            val snippet = result.get()
            logger.info("Getting snippet from bucket")
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
        logger.error("Snippet not found")
        throw SnippetNotFoundError("Snippet not found")
    }

    override fun getAssets(userId: String): List<SnippetDTO> {
        logger.info("Getting snippets by user id")
        val permissions = permissionService.getUserPermissionsByUserId(userId).body!!
        logger.info("Getting snippets where user has permissions in permission service")
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
        logger.info("Finding snippet by id")
        val snippetOptional = snippetRepository.findById(assetId)
        if (snippetOptional.isPresent && hasPermissions(userId, assetId)) {
            logger.info("Snippet found and user has permissions to update")
            val snippet = snippetOptional.get()
            logger.info("Create or get rules linting for user")
            val lintingRules = linterRuleService.createOrGetRules(userId)
            val lintingRulesToString = parseLintingRulesToString(lintingRules)
            val lintingResult = applyRules(snippet, input.content, lintingRulesToString)
            logger.info("Updating snippet")
            val snippetCopy = snippet.copy(updatedAt = Date(), compliance = lintingResult)
            snippetRepository.save(snippetCopy)
            logger.info("Updating snippet in bucket")
            objectStoreService.update(input.content, assetId)
            logger.info("Snippet updated")
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
        logger.info("Finding snippet by id")
        val result = snippetRepository.findById(assetId)
        if (result.isPresent && isOwner(assetId, userId)) {
            logger.info("Snippet found and user is owner")
            val objectResponse = objectStoreService.delete(assetId)
            logger.info("Snippet deleted from bucket")
            val permissionsResponse = permissionService.deletePermissionsByAssetId(assetId)
            logger.info("Permissions deleted")
            if (objectResponse.statusCode.is2xxSuccessful && permissionsResponse.statusCode.is2xxSuccessful) {
                logger.info("Deleting snippet from database")
                snippetRepository.deleteById(assetId)
                return "Snippet deleted with id $assetId"
            } else {
                logger.error("Error while deleting snippet from bucket")
                throw SnippetDeleteError("Error while deleting snippet from bucket")
            }
        }
        logger.error("Snippet not found")
        throw SnippetNotFoundError("Snippet not found")
    }

    override fun shareAsset(
        userId: String,
        shareDTO: ShareDTO,
    ) {
        logger.info("Finding snippet by id")
        val result = snippetRepository.findById(shareDTO.assetId)
        if (result.isPresent && isOwner(shareDTO.assetId, userId)) {
            logger.info("Snippet found and user is owner")
            permissionService.create(shareDTO.userId, shareDTO.assetId, PermissionDTO("read", shareDTO.userName))
        } else {
            logger.error("Snippet not found")
            throw SnippetNotFoundError("Snippet not found")
        }
    }

    fun getUsersToShareSnippetWith(
        snippetId: UUID,
        ownerId: String,
    ): List<UserShareDTO> {
        logger.info("Finding snippet by id")
        val result = snippetRepository.findById(snippetId)
        if (result.isEmpty) {
            logger.error("Snippet not found")
            throw SnippetNotFoundError("Snippet not found")
        }
        if (isOwner(snippetId, ownerId)) {
            logger.info("Snippet found and user is owner")
            val permissions = permissionService.getUsersWhoNotHavePermissionWithAsset(snippetId, ownerId).body!!
            return permissions.map { permission ->
                UserShareDTO(permission.userId, permission.userName)
            }
        } else {
            logger.error("User is not the owner of the snippet")
            throw SnippetShareError("You are not the owner of the snippet")
        }
    }

    fun executeSnippet(
        snippetId: UUID,
        userId: String,
        executeInput: ExecutorInput,
    ): ExecutorOutput {
        logger.info("Finding snippet by id")
        val snippet = snippetRepository.findById(snippetId)
        if (snippet.isPresent) {
            logger.info("Snippet found")
            if (!hasPermissions(userId, snippetId)) {
                logger.error("User has not permissions to run snippet")
                throw SnippetPermissionError("User has not permissions to run snippet")
            }
            logger.info("Running snippet")
            val result = runnerService.execute(executeInput)
            return result
        }
        logger.error("Snippet not found")
        throw SnippetNotFoundError("Snippet not found")
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

    private fun hasPermissions(
        userId: String,
        snippetId: UUID,
    ): Boolean {
        val response = permissionService.getUserPermissionByAssetId(snippetId, userId).body!!
        return response.permission != "read"
    }
}
