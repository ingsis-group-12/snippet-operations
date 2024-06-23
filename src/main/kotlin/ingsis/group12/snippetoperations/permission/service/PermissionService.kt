package ingsis.group12.snippetoperations.permission.service

import ingsis.group12.snippetoperations.asset.dto.PermissionDTO
import ingsis.group12.snippetoperations.permission.model.Permission
import ingsis.group12.snippetoperations.permission.model.UserWithoutPermission
import org.springframework.http.ResponseEntity
import java.util.UUID

interface PermissionService {
    fun create(
        userId: String,
        assetId: UUID,
        permission: PermissionDTO,
    ): ResponseEntity<Permission>

    fun getUserPermissionByAssetId(
        assetId: UUID,
        userId: String,
    ): ResponseEntity<Permission>

    fun getUserPermissionsByUserId(userId: String): ResponseEntity<List<Permission>>

    fun getUsersWhoNotHavePermissionWithAsset(
        assetId: UUID,
        userId: String,
    ): ResponseEntity<List<UserWithoutPermission>>

    fun updatePermission(
        userId: String,
        assetId: UUID,
        permission: PermissionDTO,
    ): ResponseEntity<Permission>

    fun deletePermissionsByAssetId(assetId: UUID): ResponseEntity<Unit>
}
