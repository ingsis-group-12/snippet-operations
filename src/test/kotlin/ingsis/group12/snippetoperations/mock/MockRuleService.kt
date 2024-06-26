package ingsis.group12.snippetoperations.mock

import ingsis.group12.snippetoperations.rule.dto.FormatterRules
import ingsis.group12.snippetoperations.rule.dto.LinterRuleInput
import ingsis.group12.snippetoperations.rule.dto.RunRuleDTO
import ingsis.group12.snippetoperations.rule.service.RuleService
import ingsis.group12.snippetoperations.runner.output.FormatterOutput
import ingsis.group12.snippetoperations.runner.output.LinterOutput

class MockFormatterRuleService : RuleService<FormatterRules, FormatterOutput> {
    override fun createOrGetRules(userId: String): FormatterRules {
        return FormatterRules(listOf())
    }

    override suspend fun updateRules(
        userId: String,
        rules: FormatterRules,
    ): FormatterRules {
        return rules
    }

    override fun runRules(
        userId: String,
        runRuleDTO: RunRuleDTO,
    ): FormatterOutput {
        return FormatterOutput("", "")
    }
}

class MockLinterRuleService : RuleService<LinterRuleInput, LinterOutput> {
    override fun createOrGetRules(userId: String): LinterRuleInput {
        return LinterRuleInput(true, true, "snake_case")
    }

    override suspend fun updateRules(
        userId: String,
        rules: LinterRuleInput,
    ): LinterRuleInput {
        return rules
    }

    override fun runRules(
        userId: String,
        runRuleDTO: RunRuleDTO,
    ): LinterOutput {
        return LinterOutput("ReportSuccess", "")
    }
}
