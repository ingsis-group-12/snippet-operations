package ingsis.group12.snippetoperations.bucket

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
class AzureObjectStoreService(
    @Value("\${bucket.url}") private val bucketUrl: String,
) : ObjectStoreService {
    @Autowired
    private lateinit var restTemplate: RestTemplate

    override fun create(
        content: String,
        assetId: UUID,
    ): ResponseEntity<String> {
        val url = "$bucketUrl/$assetId"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(content, headers)
        return restTemplate.postForEntity(url, entity, String::class.java)
    }

    override fun get(assetId: UUID): ResponseEntity<String> {
        val url = "$bucketUrl/$assetId"
        return restTemplate.getForEntity(url, String::class.java)
    }

    override fun delete(assetId: UUID): ResponseEntity<String> {
        val url = "$bucketUrl/$assetId"
        return restTemplate.exchange(url, HttpMethod.DELETE, null, String::class.java)
    }
}
