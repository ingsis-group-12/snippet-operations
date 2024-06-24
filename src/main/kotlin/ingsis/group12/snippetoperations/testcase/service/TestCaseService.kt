package ingsis.group12.snippetoperations.testcase.service

import ingsis.group12.snippetoperations.asset.repository.SnippetRepository
import ingsis.group12.snippetoperations.bucket.ObjectStoreService
import ingsis.group12.snippetoperations.exception.SnippetNotFoundError
import ingsis.group12.snippetoperations.exception.SnippetPermissionError
import ingsis.group12.snippetoperations.permission.service.PermissionService
import ingsis.group12.snippetoperations.testcase.dto.EnvironmentInput
import ingsis.group12.snippetoperations.testcase.dto.ExecutorInput
import ingsis.group12.snippetoperations.testcase.dto.ExecutorOutput
import ingsis.group12.snippetoperations.testcase.dto.TestCaseDTO
import ingsis.group12.snippetoperations.testcase.dto.TestCaseResponseDTO
import ingsis.group12.snippetoperations.testcase.dto.TestCaseResultDTO
import ingsis.group12.snippetoperations.testcase.model.TestCase
import ingsis.group12.snippetoperations.testcase.repository.TestCaseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.UUID

@Service
class TestCaseService(
    @Value("\${runner.url}") private val runnerUrl: String,
    private val testCaseRepository: TestCaseRepository,
    private val snippetRepository: SnippetRepository,
    private val objectStoreService: ObjectStoreService,
    private val permissionService: PermissionService,
) {
    @Autowired
    private lateinit var restTemplate: RestTemplate

    // TODO: Add implementation on bucket with this to store inputs,
    // outputs and environment variables of test cases
    fun createTestCase(
        snippetId: UUID,
        testCaseDTO: TestCaseDTO,
    ): TestCaseResponseDTO {
        val snippet = snippetRepository.findById(snippetId)
        if (snippet.isPresent) {
            val testCase =
                TestCase(
                    snippet = snippet.get(),
                    name = testCaseDTO.name,
                    inputs = testCaseDTO.inputs!!.joinToString(separator = "\n"),
                    outputs = testCaseDTO.outputs!!.joinToString(separator = "\n"),
                    environmentVariables = testCaseDTO.environment!!.joinToString(";") { "${it.key}=${it.value}" },
                )
            val testCaseSaved = testCaseRepository.save(testCase)
            return convertToTestCaseResponse(testCaseSaved)
        } else {
            throw SnippetNotFoundError("Snippet not found")
        }
    }

    fun getTestCasesBySnippetId(snippetId: UUID): List<TestCaseResponseDTO> {
        val testCases = testCaseRepository.getTestCasesBySnippetId(snippetId)
        return testCases.map { testCase ->
            convertToTestCaseResponse(testCase)
        }
    }

    fun updateTestCase(
        testCaseId: UUID,
        testCaseDTO: TestCaseDTO,
    ): TestCaseDTO {
        val optionalTestCase = testCaseRepository.findById(testCaseId)
        if (optionalTestCase.isPresent) {
            val testCase = optionalTestCase.get()
            val updatedTestCase =
                testCase.copy(
                    name = testCaseDTO.name,
                    inputs = testCaseDTO.inputs!!.joinToString(separator = "\n"),
                    outputs = testCaseDTO.outputs!!.joinToString(separator = "\n"),
                    environmentVariables = testCaseDTO.environment!!.joinToString(";") { "${it.key}=${it.value}" },
                )
            testCaseRepository.save(updatedTestCase)
            return testCaseDTO
        } else {
            throw SnippetNotFoundError("Test case not found")
        }
    }

    fun deleteTestCase(testCaseId: UUID) {
        testCaseRepository.deleteById(testCaseId)
    }

    fun runTestCase(
        testCaseId: UUID,
        userId: String,
    ): TestCaseResultDTO {
        val testCase = findTestCaseById(testCaseId)
        if (canExecuteTestCase(testCase, userId)) {
            throw SnippetPermissionError("User does not have permission to run this test case")
        }
        val inputs = parseInputs(testCase.inputs)
        val expectedOutputs = parseOutputs(testCase.outputs)
        val environmentVariables = parseEnvironmentVariables(testCase.environmentVariables)
        val snippetContent = getSnippetContent(testCase.snippet!!.id!!)

        val executorInput =
            ExecutorInput(
                content = snippetContent,
                inputs = inputs,
                env = environmentVariables,
            )

        val executorOutput = executeTestCase(executorInput)

        return evaluateTestCaseResult(executorOutput, expectedOutputs)
    }

    private fun canExecuteTestCase(
        testCase: TestCase,
        userId: String,
    ): Boolean {
        val response = permissionService.getUserPermissionByAssetId(testCase.snippet!!.id!!, userId).body!!
        return response.permission != "read"
    }

    private fun findTestCaseById(testCaseId: UUID): TestCase {
        val testCaseOptional = testCaseRepository.findById(testCaseId)
        if (!testCaseOptional.isPresent) {
            throw SnippetNotFoundError("Test case not found")
        }
        return testCaseOptional.get()
    }

    private fun parseInputs(inputs: String): List<String> {
        if (inputs.isEmpty()) {
            return emptyList()
        }
        return inputs.split("\n")
    }

    private fun parseOutputs(outputs: String): List<String> {
        if (outputs.isEmpty()) {
            return emptyList()
        }
        return outputs.split("\n")
    }

    private fun parseEnvironmentVariables(environmentVariables: String): List<EnvironmentInput> {
        if (environmentVariables.isEmpty()) {
            return emptyList()
        }
        return environmentVariables.split(";").map {
            val (key, value) = it.split("=")
            EnvironmentInput(key.trim(), value.trim())
        }
    }

    private fun getSnippetContent(snippetId: UUID): String {
        return objectStoreService.get(snippetId).body!!
    }

    private fun executeTestCase(executorInput: ExecutorInput): ExecutorOutput {
        val executeUrl = "$runnerUrl/interpret"
        val response = restTemplate.postForEntity(executeUrl, executorInput, ExecutorOutput::class.java)
        return response.body!!
    }

    private fun evaluateTestCaseResult(
        executorOutput: ExecutorOutput,
        expectedOutputs: List<String>,
    ): TestCaseResultDTO {
        if (executorOutput.outputs.size != expectedOutputs.size) {
            return TestCaseResultDTO(
                passed = false,
                error =
                    "Should define outputs correctly for the test case, " +
                        "current outputs: ${executorOutput.outputs}, expected outputs: $expectedOutputs",
            )
        }
        val allOutputsMatch = expectedOutputs.all { it in executorOutput.outputs }
        return TestCaseResultDTO(passed = allOutputsMatch, error = if (allOutputsMatch) null else executorOutput.error)
    }

    private fun convertToTestCaseResponse(testCase: TestCase) =
        TestCaseResponseDTO(
            id = testCase.id,
            testCase.name,
            parseInputs(testCase.inputs),
            parseOutputs(testCase.outputs),
            parseEnvironmentVariables(testCase.environmentVariables),
        )
}
