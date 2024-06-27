package ingsis.group12.snippetoperations.permission.service

import ingsis.group12.snippetoperations.asset.dto.PermissionDTO
import ingsis.group12.snippetoperations.permission.model.Permission
import ingsis.group12.snippetoperations.permission.model.SnippetPermission
import ingsis.group12.snippetoperations.permission.model.UserWithoutPermission
import ingsis.group12.snippetoperations.relic.CorrelationIdFilter.Companion.CORRELATION_ID_KEY
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
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
    private val logger = LoggerFactory.getLogger(SnippetPermissionService::class.java)

    override fun create(
        userId: String,
        assetId: UUID,
        permission: PermissionDTO,
    ): ResponseEntity<Permission> {
        val url = "$permissionUrl/$assetId/user/$userId"
        val headers = HttpHeaders()
        val correlationIdKey = MDC.get(CORRELATION_ID_KEY)
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Correlation-Id", correlationIdKey)
        logger.info("Creating permission for user $userId and asset $assetId")

        // Crea el cuerpo de la solicitud con los headers
        val requestEntity = HttpEntity(permission, headers)

        // Envía la solicitud POST con headers
        val response = restTemplate.postForEntity(url, requestEntity, SnippetPermission::class.java)
        logger.info("Permission created for user $userId and asset $assetId")
        val createdSnippetPermission = response.body
        return ResponseEntity(createdSnippetPermission, response.statusCode)
    }

    override fun getUserPermissionByAssetId(
        assetId: UUID,
        userId: String,
    ): ResponseEntity<Permission> {
        val url = "$permissionUrl/$assetId/user/$userId"
        logger.info("Getting permission for user $userId and asset $assetId")
        // Crea los headers
        val correlationIdKey = MDC.get(CORRELATION_ID_KEY)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Correlation-Id", correlationIdKey)

        // Crea HttpEntity con solo los headers
        val requestEntity = HttpEntity<Void>(headers)

        // Envía la solicitud GET con headers
        val response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, SnippetPermission::class.java)
        logger.info("Permission retrieved for user $userId and asset $assetId")
        val createdSnippetPermission = response.body
        return ResponseEntity(createdSnippetPermission, response.statusCode)
    }

    override fun getUserPermissionsByUserId(userId: String): ResponseEntity<List<Permission>> {
        logger.info("Getting permissions for user $userId")
        val url = "$permissionUrl/user/$userId"
        val correlationIdKey = MDC.get(CORRELATION_ID_KEY)
        logger.info("Permissions retrieved for user $userId")
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Correlation-Id", correlationIdKey)

        // Crea HttpEntity con solo los headers
        val requestEntity = HttpEntity<Void>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Array<SnippetPermission>::class.java)
        logger.info("Permissions retrieved for user $userId")
        val createdSnippetPermission = response.body?.toList()
        return ResponseEntity(createdSnippetPermission, response.statusCode)
    }

    override fun getUsersWhoNotHavePermissionWithAsset(
        assetId: UUID,
        userId: String,
    ): ResponseEntity<List<UserWithoutPermission>> {
        val url = "$permissionUrl/snippet/$assetId/user/$userId"
        logger.info("Getting users who do not have permission for asset $assetId")
        val correlationIdKey = MDC.get(CORRELATION_ID_KEY)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Correlation-Id", correlationIdKey)
        val requestEntity = HttpEntity<Void>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Array<UserWithoutPermission>::class.java)
        logger.info("Users who do not have permission for asset $assetId retrieved")
        val createdSnippetPermission = response.body?.toList()
        return ResponseEntity(createdSnippetPermission, response.statusCode)
    }

    override fun updatePermission(
        userId: String,
        assetId: UUID,
        permission: PermissionDTO,
    ): ResponseEntity<Permission> {
        val url = "$permissionUrl/$assetId/user/$userId"
        logger.info("Updating permission for user $userId and asset $assetId")
        val correlationIdKey = MDC.get(CORRELATION_ID_KEY)
        // Crea los headers
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Correlation-Id", correlationIdKey)

        // Crea HttpEntity con el cuerpo y los headers
        val requestEntity = HttpEntity(permission, headers)

        // Envía la solicitud PATCH con headers y cuerpo
        val response = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, SnippetPermission::class.java)

        logger.info("Permission updated for user $userId and asset $assetId")
        val createdSnippetPermission = response.body
        return ResponseEntity(createdSnippetPermission, response.statusCode)
    }

    override fun deletePermissionsByAssetId(assetId: UUID): ResponseEntity<Unit> {
        val correlationIdKey = MDC.get(CORRELATION_ID_KEY)
        logger.info("Deleting permissions for asset $assetId")

        val url = "$permissionUrl/$assetId"

        // Crea los headers
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Correlation-Id", correlationIdKey)

        // Crea HttpEntity con solo los headers
        val requestEntity = HttpEntity<Void>(headers)

        // Envía la solicitud DELETE con headers
        restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Unit::class.java)

        logger.info("Permissions deleted for asset $assetId")
        return ResponseEntity.noContent().build()
    }
}
