package ingsis.group12.snippetoperations.bucket

import org.springframework.http.ResponseEntity
import java.util.UUID

interface ObjectStoreService {
    fun create(
        content: String,
        assetId: UUID,
    ): ResponseEntity<String>

    fun get(assetId: UUID): ResponseEntity<String>

    fun delete(assetId: UUID): ResponseEntity<String>
}
