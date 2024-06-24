package ingsis.group12.snippetoperations.rule.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LinterRuleInput(
    @SerialName("enforce_literal_or_identifier_in_println_rule")
    val enforcePrintlnRule: Boolean,
    @SerialName("enforce_literal_or_identifier_in_read_input_rule")
    val enforceReadInputRule: Boolean,
    @SerialName("identifier_rule")
    val identifierRule: String,
)

@Serializable
data class LinterRules(
    val rules: List<LinterRuleInput>,
)
