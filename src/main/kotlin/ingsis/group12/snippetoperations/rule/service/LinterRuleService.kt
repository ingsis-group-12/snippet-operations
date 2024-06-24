package ingsis.group12.snippetoperations.rule.service

import ingsis.group12.snippetoperations.bucket.AzureObjectStoreService
import ingsis.group12.snippetoperations.exception.SnippetRuleError
import ingsis.group12.snippetoperations.rule.dto.LinterRuleInput
import ingsis.group12.snippetoperations.rule.dto.LinterRules
import ingsis.group12.snippetoperations.rule.model.LinterRule
import ingsis.group12.snippetoperations.rule.repository.LinterRuleRepository
import ingsis.group12.snippetoperations.rule.util.createDefaultLinterRules
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LinterRuleService(
    private val linterRuleRepository: LinterRuleRepository,
    @Value("\${linter.bucket.url}") private val linterBucketUrl: String,
) {
    @Autowired
    private val bucket = AzureObjectStoreService(linterBucketUrl)

    fun createOrGetLinterRules(userId: String): LinterRules {
        val linterRules = linterRuleRepository.findByUserId(userId)
        return if (linterRules.isPresent) {
            val linterRuleInputs = getLinterRules(linterRules.get())
            return LinterRules(linterRuleInputs)
        } else {
            createLinterRules(userId)
        }
    }

    fun updateLinterRules(
        userId: String,
        linterRules: LinterRules,
    ): LinterRules {
        val linterRule = linterRuleRepository.findByUserId(userId)
        if (linterRule.isPresent) {
            return update(linterRules, linterRule.get())
        } else {
            throw SnippetRuleError("User has not linting rules defined")
        }
    }

    private fun update(
        linterRules: LinterRules,
        linterRule: LinterRule,
    ): LinterRules {
        println(linterRules)
        val rules = parseToString(linterRules)
        bucket.update(rules, linterRule.id!!)
        return linterRules
    }

    private fun createLinterRules(userId: String): LinterRules {
        val defaultRules = createDefaultLinterRules()
        val linterRuleId = UUID.randomUUID()
        bucket.create(parseToString(defaultRules), linterRuleId)
        linterRuleRepository.save(LinterRule(id = linterRuleId, userId = userId))
        return defaultRules
    }

    private fun getLinterRules(linterRule: LinterRule): List<LinterRuleInput> {
        val rules = bucket.get(linterRule.id!!).body!!
        return parseToLinterRules(rules)
    }

    private fun parseToLinterRules(rules: String): List<LinterRuleInput> {
        val json = Json { ignoreUnknownKeys = true }
        return json.decodeFromString<List<LinterRuleInput>>(rules)
    }

    private fun parseToString(linterRules: LinterRules): String {
        val json = Json { ignoreUnknownKeys = true }
        return json.encodeToString(linterRules.rules)
    }
}
