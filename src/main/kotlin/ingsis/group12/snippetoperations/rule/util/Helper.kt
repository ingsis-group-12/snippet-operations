package ingsis.group12.snippetoperations.rule.util

import ingsis.group12.snippetoperations.rule.dto.FormatterRuleInput
import ingsis.group12.snippetoperations.rule.dto.FormatterRules
import ingsis.group12.snippetoperations.rule.dto.LinterRuleInput

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

fun createDefaultLinterRules(): LinterRuleInput {
    return LinterRuleInput(
        enforceLiteralOrIdentifierInPrintlnRule = true,
        enforceLiteralOrIdentifierInReadInputRule = true,
        identifierRule = "snake_case",
    )
}
