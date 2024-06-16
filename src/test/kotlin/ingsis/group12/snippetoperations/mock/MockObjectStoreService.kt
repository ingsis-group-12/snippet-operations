package ingsis.group12.snippetoperations.mock

import ingsis.group12.snippetoperations.bucket.ObjectStoreService
import org.springframework.http.ResponseEntity
import java.util.UUID

class MockObjectStoreService : ObjectStoreService {
    override fun create(
        content: String,
        assetId: UUID,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("Asset created")
    }

    override fun get(assetId: UUID): ResponseEntity<String> {
        return ResponseEntity.ok("let a : number = 5;")
    }

    override fun delete(assetId: UUID): ResponseEntity<String> {
        return ResponseEntity.ok("Asset deleted")
    }
}
