package ingsis.group12.snippetoperations.redis.input

import java.util.UUID

data class ProducerRequest(
    val snippetId: UUID,
    val userId: String,
)
