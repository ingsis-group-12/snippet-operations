package ingsis.group12.snippetoperations.rule.dto

import jakarta.validation.constraints.NotNull
import kotlinx.serialization.Serializable

@Serializable
data class LinterRuleInput(
    @field:NotNull(message = "enforceLiteralOrIdentifierInPrintlnRule is missing")
    val enforceLiteralOrIdentifierInPrintlnRule: Boolean,
    @field:NotNull(message = "enforceLiteralOrIdentifierInReadInputRule is missing")
    val enforceLiteralOrIdentifierInReadInputRule: Boolean,
    @field:NotNull(message = "identifierRule is missing")
    val identifierRule: String,
)
