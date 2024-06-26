package ingsis.group12.snippetoperations.rule.service

import ingsis.group12.snippetoperations.bucket.AzureObjectStoreService
import ingsis.group12.snippetoperations.exception.SnippetRuleError
import ingsis.group12.snippetoperations.permission.service.PermissionService
import ingsis.group12.snippetoperations.rule.dto.FormatterRuleInput
import ingsis.group12.snippetoperations.rule.dto.FormatterRules
import ingsis.group12.snippetoperations.rule.dto.RunRuleDTO
import ingsis.group12.snippetoperations.rule.model.FormatterRule
import ingsis.group12.snippetoperations.rule.repository.FormatterRuleRepository
import ingsis.group12.snippetoperations.rule.util.createDefaultFormatterRules
import ingsis.group12.snippetoperations.runner.input.FormatterInput
import ingsis.group12.snippetoperations.runner.output.FormatterOutput
import ingsis.group12.snippetoperations.runner.service.RunnerService
import ingsis.group12.snippetoperations.util.parseFormattingRulesToString
import ingsis.group12.snippetoperations.util.parseToFormatterRules
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID

@Service
class FormatterRuleService(
    private val formatterRuleRepository: FormatterRuleRepository,
    private val runnerService: RunnerService,
    @Value("\${formatter.bucket.url}") private val formatterBucketUrl: String,
    private val permissionService: PermissionService,
) : RuleService<FormatterRules, FormatterOutput> {
    @Autowired
    private val bucket = AzureObjectStoreService(formatterBucketUrl)

    override fun createOrGetRules(userId: String): FormatterRules {
        val formatterRules = formatterRuleRepository.findByUserId(userId)
        return if (formatterRules.isPresent) {
            val formatterRulesInputList = getFormatterRules(formatterRules.get())
            return FormatterRules(formatterRulesInputList)
        } else {
            createFormatterRules(userId)
        }
    }

    override suspend fun updateRules(
        userId: String,
        rules: FormatterRules,
    ): FormatterRules {
        val formatterRule = formatterRuleRepository.findByUserId(userId)
        if (formatterRule.isPresent) {
            return update(rules, formatterRule)
        } else {
            throw SnippetRuleError("User has not linting rules defined")
        }
    }

    override fun runRules(
        userId: String,
        runRuleDTO: RunRuleDTO,
    ): FormatterOutput {
        val formatterRules = formatterRuleRepository.findByUserId(userId)
        if (canApplyRules(userId, runRuleDTO.snippetId!!)) {
            if (formatterRules.isEmpty) {
                val result = createFormatterRules(userId)
                return runnerService.format(FormatterInput(runRuleDTO.content!!, runRuleDTO.language, result.rules))
            }
            val formatterRulesInputList = getFormatterRules(formatterRules.get())
            return runnerService.format(FormatterInput(runRuleDTO.content!!, runRuleDTO.language, formatterRulesInputList))
        } else {
            return FormatterOutput("", "User does not have permission to apply rules.")
        }
    }

    private fun update(
        formatterRules: FormatterRules,
        formatterRule: Optional<FormatterRule>,
    ): FormatterRules {
        val rules = parseFormattingRulesToString(formatterRules)
        bucket.update(rules, formatterRule.get().id!!)
        return formatterRules
    }

    private fun createFormatterRules(userId: String): FormatterRules {
        val defaultRules = createDefaultFormatterRules()
        val formatterRuleId = UUID.randomUUID()
        bucket.create(parseFormattingRulesToString(defaultRules), formatterRuleId)
        formatterRuleRepository.save(FormatterRule(id = formatterRuleId, userId = userId))
        return defaultRules
    }

    private fun getFormatterRules(formatterRule: FormatterRule): List<FormatterRuleInput> {
        val rulesJson = bucket.get(formatterRule.id!!).body!!
        return parseToFormatterRules(rulesJson)
    }

    private fun canApplyRules(
        userId: String,
        snippetId: UUID,
    ): Boolean {
        val response = permissionService.getUserPermissionByAssetId(snippetId, userId).body!!
        return response.permission != "read"
    }
}
