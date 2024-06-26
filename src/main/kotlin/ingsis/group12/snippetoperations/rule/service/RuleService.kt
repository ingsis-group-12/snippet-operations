package ingsis.group12.snippetoperations.rule.service

import ingsis.group12.snippetoperations.rule.dto.RunRuleDTO

interface RuleService<Rules, Output> {
    fun createOrGetRules(userId: String): Rules

    suspend fun updateRules(
        userId: String,
        rules: Rules,
    ): Rules

    fun runRules(
        userId: String,
        runRuleDTO: RunRuleDTO,
    ): Output
}
