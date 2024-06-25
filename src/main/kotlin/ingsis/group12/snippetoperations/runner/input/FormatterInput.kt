package ingsis.group12.snippetoperations.runner.input

import ingsis.group12.snippetoperations.rule.dto.FormatterRuleInput

data class FormatterInput(
    val content: String,
    val language: String? = "printscript 1.1",
    val rules: List<FormatterRuleInput>?,
)
