package ingsis.group12.snippetoperations.mock

import ingsis.group12.snippetoperations.asset.dto.PermissionDTO
import ingsis.group12.snippetoperations.permission.model.Permission
import ingsis.group12.snippetoperations.permission.model.UserWithoutPermission
import ingsis.group12.snippetoperations.permission.service.PermissionService
import org.springframework.http.ResponseEntity
import java.util.UUID

class MockPermissionServiceWithBadResponse : PermissionService {
    override fun create(
        userId: String,
        assetId: UUID,
        permission: PermissionDTO,
    ): ResponseEntity<Permission> {
        return ResponseEntity.badRequest().build()
    }

    override fun getUserPermissionByAssetId(
        assetId: UUID,
        userId: String,
    ): ResponseEntity<Permission> {
        return ResponseEntity.badRequest().build()
    }

    override fun getUserPermissionsByUserId(userId: String): ResponseEntity<List<Permission>> {
        return ResponseEntity.badRequest().build()
    }

    override fun updatePermission(
        userId: String,
        assetId: UUID,
        permission: PermissionDTO,
    ): ResponseEntity<Permission> {
        return ResponseEntity.badRequest().build()
    }

    override fun deletePermissionsByAssetId(assetId: UUID): ResponseEntity<Unit> {
        return ResponseEntity.badRequest().build()
    }

    override fun getUsersWhoNotHavePermissionWithAsset(
        assetId: UUID,
        userId: String,
    ): ResponseEntity<List<UserWithoutPermission>> {
        return ResponseEntity.badRequest().build()
    }
}
