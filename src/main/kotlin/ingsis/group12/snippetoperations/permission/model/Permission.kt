package ingsis.group12.snippetoperations.permission.model

import java.util.UUID

interface Permission {
    val assetId: UUID
    val permission: String
}
