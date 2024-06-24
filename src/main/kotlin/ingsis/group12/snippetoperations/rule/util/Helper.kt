package ingsis.group12.snippetoperations.rule.util

import ingsis.group12.snippetoperations.rule.dto.FormatterRuleInput
import ingsis.group12.snippetoperations.rule.dto.FormatterRules
import ingsis.group12.snippetoperations.rule.dto.LinterRuleInput
import ingsis.group12.snippetoperations.rule.dto.LinterRules

fun createDefaultFormatterRules(): FormatterRules {
    return FormatterRules(
        listOf(
            FormatterRuleInput("spaceBeforeColon", false, 0),
            FormatterRuleInput("spaceAfterColon", true, 0),
            FormatterRuleInput("spaceAroundEqual", true, 0),
            FormatterRuleInput("newlineBeforePrintln", false, 1),
            FormatterRuleInput("spacesAfterStartLine", true, 3),
        ),
    )
}

fun createDefaultLinterRules(): LinterRules {
    return LinterRules(
        listOf(
            LinterRuleInput(
                enforcePrintlnRule = true,
                enforceReadInputRule = true,
                identifierRule = "snake_case",
            ),
        ),
    )
}

// {
//    "enforce_literal_or_identifier_in_println_rule": true,
//    "enforce_literal_or_identifier_in_read_input_rule": true,
//    "identifier_rule": "snake_case"
// }
