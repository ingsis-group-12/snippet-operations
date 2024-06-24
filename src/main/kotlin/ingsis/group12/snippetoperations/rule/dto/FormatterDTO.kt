package ingsis.group12.snippetoperations.rule.dto

import kotlinx.serialization.Serializable

@Serializable
data class FormatterRuleInput(
    val type: String,
    val allowed: Boolean,
    val maxInt: Int,
)

@Serializable
data class FormatterRules(
    val rules: List<FormatterRuleInput>,
)
