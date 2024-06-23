package ingsis.group12.snippetoperations.asset.input

import jakarta.validation.constraints.NotNull

data class SnippetInput(
    @field:NotNull(message = "name type is missing")
    override val name: String,
    @field:NotNull(message = "content type is missing")
    override val content: String,
    @field:NotNull(message = "property language is missing")
    val language: String,
    @field:NotNull(message = "property type is missing")
    val extension: String,
    @field:NotNull(message = "userName inputs is missing")
    val userName: String,
) : AssetInput

data class SnippetUpdateInput(
    @field:NotNull(message = "name type is missing")
    override val name: String,
    @field:NotNull(message = "content type is missing")
    override val content: String,
) : AssetInput
