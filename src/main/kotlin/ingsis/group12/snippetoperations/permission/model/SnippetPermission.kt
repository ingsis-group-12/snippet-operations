package ingsis.group12.snippetoperations.permission.model

import java.util.UUID

data class SnippetPermission(
    override val permission: String,
    val snippetId: UUID,
    val userId: String,
    val userName: String,
) : Permission {
    override val assetId: UUID
        get() = snippetId
}

data class UserWithoutPermission(
    val userId: String,
    val userName: String,
)
