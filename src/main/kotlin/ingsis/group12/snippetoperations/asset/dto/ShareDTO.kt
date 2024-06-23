package ingsis.group12.snippetoperations.asset.dto

import java.util.UUID

data class ShareDTO(
    val assetId: UUID,
    val userId: String,
    val userName: String,
)

data class UserShareDTO(
    val userId: String,
    val userName: String,
)
