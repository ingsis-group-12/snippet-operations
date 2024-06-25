package ingsis.group12.snippetoperations.rule.dto

import jakarta.validation.constraints.NotNull
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class FormatterRuleInput(
    @field:NotNull(message = "type of formatting rule is missing")
    val type: String? = null,
    @field:NotNull(message = "allowed of formatting rule is missing")
    val allowed: Boolean? = null,
    val maxInt: Int? = null,
)

@Serializable
data class FormatterRules(
    val rules: List<FormatterRuleInput>,
)

data class RunRuleDTO(
    @field:NotNull(message = "snippetId is missing")
    val snippetId: UUID? = null,
    @field:NotNull(message = "content of formatting rule is missing")
    val content: String? = null,
    @field:NotNull(message = "content of language is missing")
    val language: String? = null,
)
