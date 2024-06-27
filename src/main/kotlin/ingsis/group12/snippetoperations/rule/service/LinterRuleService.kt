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
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(LinterRuleService::class.java)

    override fun createOrGetRules(userId: String): LinterRuleInput {
        logger.info("Creating or getting linting rules for user $userId")
        val linterRules = linterRuleRepository.findByUserId(userId)
        return if (linterRules.isPresent) {
            logger.info("Rules found for user $userId")
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
        logger.info("Updating rules for user $userId")
        val linterRule = linterRuleRepository.findByUserId(userId)
        logger.info("Rules found for user $userId")
        if (linterRule.isPresent) {
            return update(rules, linterRule.get(), userId)
        } else {
            logger.error("User has not linting rules defined")
            throw SnippetRuleError("User has not linting rules defined")
        }
    }

    override fun runRules(
        userId: String,
        runRuleDTO: RunRuleDTO,
    ): LinterOutput {
        logger.info("Running rules for user $userId")
        val linterRules = linterRuleRepository.findByUserId(userId)

        if (!canApplyRules(userId, runRuleDTO.snippetId!!)) {
            logger.error("User does not have permission to apply rules")
            return LinterOutput("", "User does not have permission to apply rules.")
        }

        val linterRulesInString =
            if (linterRules.isEmpty) {
                logger.info("Creating rules for user $userId")
                val linterRuleInput = createLinterRules(userId)
                parseLintingRulesToString(linterRuleInput)
            } else {
                logger.info("Rules found for user $userId")
                val result = getLinterRules(linterRules.get())
                parseLintingRulesToString(result)
            }
        logger.info("Analyzing code snippet")
        val lintResult = runnerService.analyze(LinterInput(runRuleDTO.content!!, runRuleDTO.language!!, linterRulesInString))
        logger.info("Code snippet analyzed")
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
        logger.info("Executing producer for user $userId")
        snippets.forEach {
            producer.publishEvent(ProducerRequest(snippetId = it.assetId, userId))
        }
    }

    private fun createLinterRules(userId: String): LinterRuleInput {
        val defaultRules = createDefaultLinterRules()
        val linterRuleId = UUID.randomUUID()
        logger.info("Creating rules for user $userId")
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
