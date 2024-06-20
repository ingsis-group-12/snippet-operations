package ingsis.group12.snippetoperations.permission.model

import java.util.UUID

data class SnippetPermission(
    override val permission: String,
    val userId: String,
    val id: UUID,
) : Permission {
    override val assetId: UUID
        get() = snippetId

    private val snippetId: UUID
        get() = assetId
}
