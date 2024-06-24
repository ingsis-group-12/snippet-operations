package ingsis.group12.snippetoperations.rule.service

import ingsis.group12.snippetoperations.bucket.AzureObjectStoreService
import ingsis.group12.snippetoperations.exception.SnippetRuleError
import ingsis.group12.snippetoperations.rule.dto.FormatterRuleInput
import ingsis.group12.snippetoperations.rule.dto.FormatterRules
import ingsis.group12.snippetoperations.rule.model.FormatterRule
import ingsis.group12.snippetoperations.rule.repository.FormatterRuleRepository
import ingsis.group12.snippetoperations.rule.util.createDefaultFormatterRules
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID

@Service
class FormatterRuleService(
    private val formatterRuleRepository: FormatterRuleRepository,
    @Value("\${formatter.bucket.url}") private val formatterBucketUrl: String,
) {
    @Autowired
    private val bucket = AzureObjectStoreService(formatterBucketUrl)

    fun createOrGetFormatterRules(userId: String): FormatterRules {
        val formatterRules = formatterRuleRepository.findByUserId(userId)
        return if (formatterRules.isPresent) {
            val formatterRulesInputList = getFormatterRules(formatterRules.get())
            return FormatterRules(formatterRulesInputList)
        } else {
            createFormatterRules(userId)
        }
    }

    fun updateFormatterRules(
        userId: String,
        formatterRules: FormatterRules,
    ): FormatterRules {
        val formatterRule = formatterRuleRepository.findByUserId(userId)
        if (formatterRule.isPresent) {
            return update(formatterRules, formatterRule)
        } else {
            throw SnippetRuleError("User has not linting rules defined")
        }
    }

    private fun update(
        formatterRules: FormatterRules,
        formatterRule: Optional<FormatterRule>,
    ): FormatterRules {
        val rules = parseToString(formatterRules)
        bucket.update(rules, formatterRule.get().id!!)
        return formatterRules
    }

    private fun createFormatterRules(userId: String): FormatterRules {
        val defaultRules = createDefaultFormatterRules()
        val formatterRuleId = UUID.randomUUID()
        bucket.create(parseToString(defaultRules), formatterRuleId)
        formatterRuleRepository.save(FormatterRule(id = formatterRuleId, userId = userId))
        return defaultRules
    }

    private fun getFormatterRules(formatterRule: FormatterRule): List<FormatterRuleInput> {
        val rulesJson = bucket.get(formatterRule.id!!).body!!
        return parseToFormatterRules(rulesJson)
    }

    private fun parseToFormatterRules(rulesJson: String): List<FormatterRuleInput> {
        val json = Json { ignoreUnknownKeys = true }
        return json.decodeFromString<List<FormatterRuleInput>>(rulesJson)
    }

    private fun parseToString(formatterRules: FormatterRules): String {
        val json = Json { ignoreUnknownKeys = true }
        return json.encodeToString(formatterRules.rules)
    }
}
