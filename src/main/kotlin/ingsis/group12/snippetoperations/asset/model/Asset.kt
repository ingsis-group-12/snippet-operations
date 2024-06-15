package ingsis.group12.snippetoperations.asset.model

import java.util.UUID

interface Asset {
    val id: UUID
    val name: String
    val content: String
}
