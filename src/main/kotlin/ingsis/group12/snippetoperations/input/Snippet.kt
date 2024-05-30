package ingsis.group12.snippetoperations.input

import jakarta.validation.constraints.NotNull

data class Snippet(
    @field:NotNull(message = "property name is missing")
    val name: String?,
    @field:NotNull(message = "property content is missing")
    val content: String?,
    @field:NotNull(message = "property language is missing")
    val language: String?,
    @field:NotNull(message = "property extension is missing")
    val extension: String?,
)
