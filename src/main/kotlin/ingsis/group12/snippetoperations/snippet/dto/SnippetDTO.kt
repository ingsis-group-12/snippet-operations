package ingsis.group12.snippetoperations.snippet.dto

import java.util.UUID

data class SnippetDTO(
    val id: UUID,
    val name: String,
    val content: String,
    val language: String,
    val extension: String,
)
