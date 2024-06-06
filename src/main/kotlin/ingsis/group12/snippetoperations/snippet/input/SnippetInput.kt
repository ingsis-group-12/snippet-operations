package ingsis.group12.snippetoperations.snippet.input

import jakarta.validation.constraints.NotNull

data class SnippetInput(
    @field:NotNull(message = "property name is missing")
    val name: String,
    @field:NotNull(message = "property content is missing")
    val content: String,
    @field:NotNull(message = "property language is missing")
    val language: String,
    @field:NotNull(message = "property type is missing")
    val extension: String,
)
