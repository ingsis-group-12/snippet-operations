package ingsis.group12.snippetoperations.mock

import ingsis.group12.snippetoperations.asset.dto.PermissionDTO
import ingsis.group12.snippetoperations.permission.model.Permission
import ingsis.group12.snippetoperations.permission.model.SnippetPermission
import ingsis.group12.snippetoperations.permission.service.PermissionService
import org.springframework.http.ResponseEntity
import java.util.UUID

class MockPermissionService : PermissionService {
    override fun create(
        userId: String,
        assetId: UUID,
        permission: PermissionDTO,
    ): ResponseEntity<Permission> {
        return ResponseEntity.ok(SnippetPermission(permission.permission, assetId, userId))
    }

    override fun getUserPermissionByAssetId(
        assetId: UUID,
        userId: String,
    ): ResponseEntity<Permission> {
        return ResponseEntity.ok(SnippetPermission("owner", assetId, userId))
    }

    override fun getUserPermissionsByUserId(userId: String): ResponseEntity<List<Permission>> {
        return ResponseEntity.ok(
            listOf(
                SnippetPermission("owner", UUID.randomUUID(), userId),
                SnippetPermission("viewer", UUID.randomUUID(), userId),
            ),
        )
    }

    override fun updatePermission(
        userId: String,
        assetId: UUID,
        permission: PermissionDTO,
    ): ResponseEntity<Permission> {
        return ResponseEntity.ok(SnippetPermission(permission.permission, assetId, userId))
    }

    override fun deletePermissionsByAssetId(assetId: UUID): ResponseEntity<Unit> {
        return ResponseEntity.noContent().build()
    }
}
