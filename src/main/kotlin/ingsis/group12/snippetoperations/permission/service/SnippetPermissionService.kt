package ingsis.group12.snippetoperations.permission.service

import ingsis.group12.snippetoperations.asset.dto.PermissionDTO
import ingsis.group12.snippetoperations.permission.model.Permission
import ingsis.group12.snippetoperations.permission.model.SnippetPermission
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.UUID

@Service
class SnippetPermissionService(
    @Value("\${permissions.url}") private val permissionUrl: String,
) : PermissionService {
    @Autowired
    private lateinit var restTemplate: RestTemplate

    override fun create(
        userId: String,
        assetId: UUID,
        permission: PermissionDTO,
    ): ResponseEntity<Permission> {
        val url = "$permissionUrl/$assetId/user/$userId"
        val response = restTemplate.postForEntity(url, permission, SnippetPermission::class.java)
        val createdSnippetPermission = response.body
        return ResponseEntity(createdSnippetPermission, response.statusCode)
    }

    override fun getUserPermissionByAssetId(
        assetId: UUID,
        userId: String,
    ): ResponseEntity<Permission> {
        val url = "$permissionUrl/$assetId/user/$userId"
        val response = restTemplate.getForEntity(url, SnippetPermission::class.java)
        val createdSnippetPermission = response.body
        return ResponseEntity(createdSnippetPermission, response.statusCode)
    }

    override fun updatePermission(
        userId: String,
        assetId: UUID,
        permission: PermissionDTO,
    ): ResponseEntity<Permission> {
        val url = "$permissionUrl/$assetId/user/$userId"
        val response = restTemplate.exchange(url, HttpMethod.PATCH, HttpEntity(permission), SnippetPermission::class.java)
        val createdSnippetPermission = response.body
        return ResponseEntity(createdSnippetPermission, response.statusCode)
    }
}