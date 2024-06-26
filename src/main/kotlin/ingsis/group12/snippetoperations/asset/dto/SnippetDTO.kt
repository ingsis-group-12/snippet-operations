package ingsis.group12.snippetoperations.asset.dto

import ingsis.group12.snippetoperations.asset.model.Asset
import ingsis.group12.snippetoperations.asset.model.ComplianceType
import java.util.UUID

data class SnippetDTO(
    override val id: UUID,
    override val name: String,
    override val content: String,
    val language: String,
    val extension: String,
    val userName: String? = null,
    val userId: String? = null,
    val complianceType: ComplianceType?,
) : Asset
