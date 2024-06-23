package ingsis.group12.snippetoperations.asset.service

import ingsis.group12.snippetoperations.asset.dto.PermissionDTO
import ingsis.group12.snippetoperations.asset.dto.ShareDTO
import ingsis.group12.snippetoperations.asset.dto.SnippetDTO
import ingsis.group12.snippetoperations.asset.dto.UserShareDTO
import ingsis.group12.snippetoperations.asset.input.AssetInput
import ingsis.group12.snippetoperations.asset.input.SnippetInput
import ingsis.group12.snippetoperations.asset.input.SnippetUpdateInput
import ingsis.group12.snippetoperations.asset.model.Snippet
import ingsis.group12.snippetoperations.asset.repository.SnippetRepository
import ingsis.group12.snippetoperations.bucket.ObjectStoreService
import ingsis.group12.snippetoperations.exception.SnippetCreationError
import ingsis.group12.snippetoperations.exception.SnippetDeleteError
import ingsis.group12.snippetoperations.exception.SnippetNotFoundError
import ingsis.group12.snippetoperations.exception.SnippetShareError
import ingsis.group12.snippetoperations.permission.model.SnippetPermission
import ingsis.group12.snippetoperations.permission.service.PermissionService
import org.springframework.stereotype.Service
import java.util.Date
import java.util.UUID

@Service
class SnippetService(
    private val snippetRepository: SnippetRepository,
    private val objectStoreService: ObjectStoreService,
    private val permissionService: PermissionService,
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
            )
        }
    }

    override fun updateAsset(
        assetId: UUID,
        assetInput: AssetInput,
        userId: String,
    ): SnippetDTO {
        val input = assetInput as SnippetUpdateInput
        val result = snippetRepository.findById(assetId)
        if (result.isPresent && isOwner(assetId, userId)) {
            val snippet = result.get()
            snippet.updatedAt = Date()
            snippetRepository.save(snippet)
            objectStoreService.update(input.content, assetId)
            return SnippetDTO(
                assetId,
                input.name,
                input.content,
                snippet.language!!,
                snippet.extension!!,
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

    private fun saveSnippet(
        snippetInput: SnippetInput,
        snippetId: UUID,
        userId: String,
    ): SnippetDTO {
        snippetRepository.save(
            Snippet(
                id = snippetId,
                name = snippetInput.name,
                language = snippetInput.language,
                extension = snippetInput.extension,
            ),
        )
        return SnippetDTO(
            snippetId,
            snippetInput.name,
            snippetInput.content,
            snippetInput.language,
            snippetInput.extension,
            snippetInput.userName,
            userId,
        )
    }

    private fun isOwner(
        assetId: UUID,
        userId: String,
    ) = permissionService.getUserPermissionByAssetId(assetId, userId).body!!.permission == "owner"
}
