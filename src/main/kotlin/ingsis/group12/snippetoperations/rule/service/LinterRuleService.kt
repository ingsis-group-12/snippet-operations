package ingsis.group12.snippetoperations.rule.service

import ingsis.group12.snippetoperations.bucket.AzureObjectStoreService
import ingsis.group12.snippetoperations.exception.SnippetRuleError
import ingsis.group12.snippetoperations.permission.service.PermissionService
import ingsis.group12.snippetoperations.redis.input.ProducerRequest
import ingsis.group12.snippetoperations.redis.producer.LinterProducer
import ingsis.group12.snippetoperations.rule.dto.LinterRuleInput
import ingsis.group12.snippetoperations.rule.dto.RunRuleDTO
import ingsis.group12.snippetoperations.rule.model.LinterRule
import ingsis.group12.snippetoperations.rule.repository.LinterRuleRepository
import ingsis.group12.snippetoperations.rule.util.createDefaultLinterRules
import ingsis.group12.snippetoperations.runner.input.LinterInput
import ingsis.group12.snippetoperations.runner.output.LinterOutput
import ingsis.group12.snippetoperations.runner.service.RunnerService
import ingsis.group12.snippetoperations.util.parseLintingRulesToString
import ingsis.group12.snippetoperations.util.parseToLinterRules
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LinterRuleService(
    private val linterRuleRepository: LinterRuleRepository,
    private val permissionService: PermissionService,
    private val runnerService: RunnerService,
    private val producer: LinterProducer,
    @Value("\${linter.bucket.url}") private val linterBucketUrl: String,
) : RuleService<LinterRuleInput, LinterOutput> {
    @Autowired
    private val bucket = AzureObjectStoreService(linterBucketUrl)

    override fun createOrGetRules(userId: String): LinterRuleInput {
        val linterRules = linterRuleRepository.findByUserId(userId)
        return if (linterRules.isPresent) {
            val linterRuleInput = getLinterRules(linterRules.get())
            return linterRuleInput
        } else {
            createLinterRules(userId)
        }
    }

    override suspend fun updateRules(
        userId: String,
        rules: LinterRuleInput,
    ): LinterRuleInput {
        val linterRule = linterRuleRepository.findByUserId(userId)
        if (linterRule.isPresent) {
            return update(rules, linterRule.get(), userId)
        } else {
            throw SnippetRuleError("User has not linting rules defined")
        }
    }

    override fun runRules(
        userId: String,
        runRuleDTO: RunRuleDTO,
    ): LinterOutput {
        val linterRules = linterRuleRepository.findByUserId(userId)

        if (!canApplyRules(userId, runRuleDTO.snippetId!!)) {
            return LinterOutput("", "User does not have permission to apply rules.")
        }

        val linterRulesInString =
            if (linterRules.isEmpty) {
                val linterRuleInput = createLinterRules(userId)
                parseLintingRulesToString(linterRuleInput)
            } else {
                val result = getLinterRules(linterRules.get())
                parseLintingRulesToString(result)
            }
        val lintResult = runnerService.analyze(LinterInput(runRuleDTO.content!!, runRuleDTO.language!!, linterRulesInString))
        return lintResult
    }

    private suspend fun update(
        linterRuleInput: LinterRuleInput,
        linterRule: LinterRule,
        userId: String,
    ): LinterRuleInput {
        val rules = parseLintingRulesToString(linterRuleInput)
        bucket.update(rules, linterRule.id!!)
        executeProducer(userId)
        return linterRuleInput
    }

    private suspend fun executeProducer(userId: String) {
        val snippets = permissionService.getUserPermissionsByUserId(userId).body!!.filter { it.permission != "read" }
        snippets.forEach {
            producer.publishEvent(ProducerRequest(snippetId = it.assetId, userId))
        }
    }

    private fun createLinterRules(userId: String): LinterRuleInput {
        val defaultRules = createDefaultLinterRules()
        val linterRuleId = UUID.randomUUID()
        bucket.create(parseLintingRulesToString(defaultRules), linterRuleId)
        linterRuleRepository.save(LinterRule(id = linterRuleId, userId = userId))
        return defaultRules
    }

    private fun getLinterRules(linterRule: LinterRule): LinterRuleInput {
        val rules = bucket.get(linterRule.id!!).body!!
        return parseToLinterRules(rules)
    }

    private fun canApplyRules(
        userId: String,
        snippetId: UUID,
    ): Boolean {
        val response = permissionService.getUserPermissionByAssetId(snippetId, userId).body!!
        return response.permission != "read"
    }
}
