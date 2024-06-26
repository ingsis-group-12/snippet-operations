package ingsis.group12.snippetoperations.util

import ingsis.group12.snippetoperations.rule.dto.FormatterRuleInput
import ingsis.group12.snippetoperations.rule.dto.FormatterRules
import ingsis.group12.snippetoperations.rule.dto.LinterRuleInput
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun parseToLinterRules(rules: String): LinterRuleInput {
    val json = Json { ignoreUnknownKeys = true }
    return json.decodeFromString<LinterRuleInput>(rules)
}

fun parseLintingRulesToString(linterRuleInput: LinterRuleInput): String {
    val json = Json { ignoreUnknownKeys = true }
    return json.encodeToString(linterRuleInput)
}

fun parseToFormatterRules(rulesJson: String): List<FormatterRuleInput> {
    val json = Json { ignoreUnknownKeys = true }
    return json.decodeFromString<List<FormatterRuleInput>>(rulesJson)
}

fun parseFormattingRulesToString(formatterRules: FormatterRules): String {
    val json = Json { ignoreUnknownKeys = true }
    return json.encodeToString(formatterRules.rules)
}
