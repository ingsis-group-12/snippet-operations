package ingsis.group12.snippetoperations.asset.service

import ingsis.group12.snippetoperations.asset.dto.PermissionDTO
import ingsis.group12.snippetoperations.asset.dto.ShareDTO
import ingsis.group12.snippetoperations.asset.dto.SnippetDTO
import ingsis.group12.snippetoperations.asset.input.SnippetInput
import ingsis.group12.snippetoperations.asset.model.Snippet
import ingsis.group12.snippetoperations.asset.repository.SnippetRepository
import ingsis.group12.snippetoperations.bucket.ObjectStoreService
import ingsis.group12.snippetoperations.exception.SnippetCreationError
import ingsis.group12.snippetoperations.exception.SnippetDeleteError
import ingsis.group12.snippetoperations.exception.SnippetNotFoundError
import ingsis.group12.snippetoperations.permission.service.PermissionService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SnippetService(
    private val snippetRepository: SnippetRepository,
    private val objectStoreService: ObjectStoreService,
    private val permissionService: PermissionService,
) : AssetService {
    override fun createAsset(
        assetInput: SnippetInput,
        userId: String,
    ): SnippetDTO {
        val snippetId = UUID.randomUUID()
        val permissionResponse = permissionService.create(userId, snippetId, PermissionDTO("owner"))
        if (permissionResponse.statusCode.is2xxSuccessful) {
            val storageResponse = objectStoreService.create(assetInput.content, snippetId)
            if (storageResponse.statusCode.is2xxSuccessful) {
                return saveSnippet(assetInput, snippetId)
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

    override fun getAssets(): List<SnippetDTO> {
        val snippets = snippetRepository.findAll()
        return snippets.map {
            val content = objectStoreService.get(it.id!!).body!!
            SnippetDTO(
                it.id,
                it.name!!,
                content,
                it.language!!,
                it.extension!!,
            )
        }
    }

    override fun deleteAssetById(assetId: UUID): String {
        val result = snippetRepository.findById(assetId)
        if (result.isPresent) {
            val response = objectStoreService.delete(assetId)
            if (response.statusCode.is2xxSuccessful) {
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
        val userPermission = permissionService.getUserPermissionByAssetId(shareDTO.assetId, userId)
        if (result.isPresent && userPermission.body!!.permission == "owner") {
            permissionService.create(shareDTO.userId, shareDTO.assetId, PermissionDTO("read:write"))
        } else {
            throw SnippetNotFoundError("Snippet not found")
        }
    }

    private fun saveSnippet(
        snippetInput: SnippetInput,
        snippetId: UUID,
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
        )
    }
}
