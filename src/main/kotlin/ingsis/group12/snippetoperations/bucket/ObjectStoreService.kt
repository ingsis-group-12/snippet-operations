package ingsis.group12.snippetoperations.bucket

import org.springframework.http.ResponseEntity
import java.util.UUID

interface ObjectStoreService {
    fun create(
        content: String,
        snippetId: UUID,
    ): ResponseEntity<String>

    fun get(snippetId: UUID): ResponseEntity<String>

    fun delete(snippetId: UUID): ResponseEntity<String>
}
