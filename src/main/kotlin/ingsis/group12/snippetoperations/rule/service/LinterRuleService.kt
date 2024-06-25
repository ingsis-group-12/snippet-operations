package ingsis.group12.snippetoperations.rule.service

import ingsis.group12.snippetoperations.asset.model.ComplianceType
import ingsis.group12.snippetoperations.asset.model.Snippet
import ingsis.group12.snippetoperations.asset.repository.SnippetRepository
import ingsis.group12.snippetoperations.bucket.AzureObjectStoreService
import ingsis.group12.snippetoperations.exception.SnippetRuleError
import ingsis.group12.snippetoperations.permission.service.PermissionService
import ingsis.group12.snippetoperations.rule.dto.LinterRuleInput
import ingsis.group12.snippetoperations.rule.dto.RunRuleDTO
import ingsis.group12.snippetoperations.rule.model.LinterRule
import ingsis.group12.snippetoperations.rule.repository.LinterRuleRepository
import ingsis.group12.snippetoperations.rule.util.createDefaultLinterRules
import ingsis.group12.snippetoperations.runner.input.LinterInput
import ingsis.group12.snippetoperations.runner.output.LinterOutput
import ingsis.group12.snippetoperations.runner.service.RunnerService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID

@Service
class LinterRuleService(
    private val linterRuleRepository: LinterRuleRepository,
    private val runnerService: RunnerService,
    private val permissionService: PermissionService,
    private val snippetRepository: SnippetRepository,
    @Value("\${linter.bucket.url}") private val linterBucketUrl: String,
) {
    @Autowired
    private val bucket = AzureObjectStoreService(linterBucketUrl)

    fun createOrGetLinterRules(userId: String): LinterRuleInput {
        val linterRules = linterRuleRepository.findByUserId(userId)
        return if (linterRules.isPresent) {
            val linterRuleInput = getLinterRules(linterRules.get())
            return linterRuleInput
        } else {
            createLinterRules(userId)
        }
    }

    fun updateLinterRules(
        userId: String,
        linterRules: LinterRuleInput,
    ): LinterRuleInput {
        val linterRule = linterRuleRepository.findByUserId(userId)
        if (linterRule.isPresent) {
            return update(linterRules, linterRule.get())
        } else {
            throw SnippetRuleError("User has not linting rules defined")
        }
    }

    fun runLinterRules(
        userId: String,
        runRuleDTO: RunRuleDTO,
    ): LinterOutput {
        val linterRules = linterRuleRepository.findByUserId(userId)
        val snippet = snippetRepository.findById(runRuleDTO.snippetId!!)

        if (!canApplyRules(userId, runRuleDTO.snippetId) || !snippet.isPresent) {
            return LinterOutput("", "User does not have permission to apply rules.")
        }

        val linterRulesInString =
            if (linterRules.isEmpty) {
                val linterRuleInput = createLinterRules(userId)
                parseToString(linterRuleInput)
            } else {
                val result = getLinterRules(linterRules.get())
                parseToString(result)
            }

        return analyzeAndHandleResult(runRuleDTO, snippet, linterRulesInString)
    }

    private fun analyzeAndHandleResult(
        runRuleDTO: RunRuleDTO,
        snippet: Optional<Snippet>,
        linterRulesInString: String,
    ): LinterOutput {
        val result =
            runnerService.analyze(
                LinterInput(runRuleDTO.content!!, runRuleDTO.language, linterRulesInString),
            )

        if (result.errors.isNotBlank()) {
            updateSnippetCompliance(result.output, snippet)
        } else {
            saveSnippetAsCompliant(snippet)
        }

        return result
    }

    private fun updateSnippetCompliance(
        output: String,
        snippet: Optional<Snippet>,
    ) {
        val updatedSnippet =
            if (output.contains("ReportFailure")) {
                snippet.get().copy(compliance = ComplianceType.NOT_COMPLIANT)
            } else {
                snippet.get().copy(compliance = ComplianceType.FAILED)
            }
        snippetRepository.save(updatedSnippet)
    }

    private fun saveSnippetAsCompliant(snippet: Optional<Snippet>) {
        val compliantSnippet = snippet.get().copy(compliance = ComplianceType.COMPLIANT)
        snippetRepository.save(compliantSnippet)
    }

    private fun update(
        linterRuleInput: LinterRuleInput,
        linterRule: LinterRule,
    ): LinterRuleInput {
        val rules = parseToString(linterRuleInput)
        bucket.update(rules, linterRule.id!!)
        return linterRuleInput
    }

    private fun createLinterRules(userId: String): LinterRuleInput {
        val defaultRules = createDefaultLinterRules()
        val linterRuleId = UUID.randomUUID()
        bucket.create(parseToString(defaultRules), linterRuleId)
        linterRuleRepository.save(LinterRule(id = linterRuleId, userId = userId))
        return defaultRules
    }

    private fun getLinterRules(linterRule: LinterRule): LinterRuleInput {
        val rules = bucket.get(linterRule.id!!).body!!
        return parseToLinterRules(rules)
    }

    private fun parseToLinterRules(rules: String): LinterRuleInput {
        val json = Json { ignoreUnknownKeys = true }
        return json.decodeFromString<LinterRuleInput>(rules)
    }

    private fun parseToString(linterRuleInput: LinterRuleInput): String {
        val json = Json { ignoreUnknownKeys = true }
        return json.encodeToString(linterRuleInput)
    }

    private fun canApplyRules(
        userId: String,
        snippetId: UUID,
    ): Boolean {
        val response = permissionService.getUserPermissionByAssetId(snippetId, userId).body!!
        return response.permission != "read"
    }
}
