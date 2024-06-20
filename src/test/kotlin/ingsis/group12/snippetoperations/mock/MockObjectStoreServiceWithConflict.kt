package ingsis.group12.snippetoperations.mock

import ingsis.group12.snippetoperations.bucket.ObjectStoreService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID

class MockObjectStoreServiceWithConflict : ObjectStoreService {
    override fun create(
        content: String,
        assetId: UUID,
    ): ResponseEntity<String> {
        return ResponseEntity("Conflict", HttpStatus.CONFLICT)
    }

    override fun get(assetId: UUID): ResponseEntity<String> {
        return ResponseEntity("Conflict", HttpStatus.CONFLICT)
    }

    override fun delete(assetId: UUID): ResponseEntity<String> {
        return ResponseEntity("Conflict", HttpStatus.CONFLICT)
    }

    override fun update(
        content: String,
        assetId: UUID,
    ): ResponseEntity<String> {
        return ResponseEntity("Conflict", HttpStatus.CONFLICT)
    }
}
