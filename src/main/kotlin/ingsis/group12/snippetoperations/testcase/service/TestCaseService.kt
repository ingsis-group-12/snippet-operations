package ingsis.group12.snippetoperations.testcase.service

import ingsis.group12.snippetoperations.asset.repository.SnippetRepository
import ingsis.group12.snippetoperations.bucket.ObjectStoreService
import ingsis.group12.snippetoperations.exception.SnippetNotFoundError
import ingsis.group12.snippetoperations.exception.SnippetPermissionError
import ingsis.group12.snippetoperations.permission.service.PermissionService
import ingsis.group12.snippetoperations.runner.input.ExecutorInput
import ingsis.group12.snippetoperations.runner.output.ExecutorOutput
import ingsis.group12.snippetoperations.runner.service.RunnerService
import ingsis.group12.snippetoperations.testcase.dto.EnvironmentInput
import ingsis.group12.snippetoperations.testcase.dto.TestCaseDTO
import ingsis.group12.snippetoperations.testcase.dto.TestCaseResponseDTO
import ingsis.group12.snippetoperations.testcase.dto.TestCaseResultDTO
import ingsis.group12.snippetoperations.testcase.model.TestCase
import ingsis.group12.snippetoperations.testcase.repository.TestCaseRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TestCaseService(
    private val testCaseRepository: TestCaseRepository,
    private val snippetRepository: SnippetRepository,
    private val objectStoreService: ObjectStoreService,
    private val runnerService: RunnerService,
    private val permissionService: PermissionService,
) {
    private val logger = LoggerFactory.getLogger(TestCaseService::class.java)

    fun createTestCase(
        snippetId: UUID,
        testCaseDTO: TestCaseDTO,
    ): TestCaseResponseDTO {
        logger.info("Creating test case for snippet $snippetId")
        val snippet = snippetRepository.findById(snippetId)
        if (snippet.isPresent) {
            logger.info("Snippet found")
            val testCase =
                TestCase(
                    snippet = snippet.get(),
                    name = testCaseDTO.name,
                    inputs = testCaseDTO.inputs!!.joinToString(separator = "\n"),
                    outputs = testCaseDTO.outputs!!.joinToString(separator = "\n"),
                    environmentVariables = testCaseDTO.environment!!.joinToString(";") { "${it.key}=${it.value}" },
                )
            logger.info("Saving test case")
            val testCaseSaved = testCaseRepository.save(testCase)
            logger.info("Test case saved")
            return convertToTestCaseResponse(testCaseSaved)
        } else {
            throw SnippetNotFoundError("Snippet not found")
        }
    }

    fun getTestCasesBySnippetId(snippetId: UUID): List<TestCaseResponseDTO> {
        logger.info("Getting test cases for snippet $snippetId")
        val testCases = testCaseRepository.getTestCasesBySnippetId(snippetId)
        logger.info("Test cases retrieved for snippet $snippetId")
        return testCases.map { testCase ->
            convertToTestCaseResponse(testCase)
        }
    }

    fun updateTestCase(
        testCaseId: UUID,
        testCaseDTO: TestCaseDTO,
    ): TestCaseDTO {
        logger.info("Updating test case $testCaseId")
        val optionalTestCase = testCaseRepository.findById(testCaseId)
        if (optionalTestCase.isPresent) {
            logger.info("Test case found")
            val testCase = optionalTestCase.get()
            val updatedTestCase =
                testCase.copy(
                    name = testCaseDTO.name,
                    inputs = testCaseDTO.inputs!!.joinToString(separator = "\n"),
                    outputs = testCaseDTO.outputs!!.joinToString(separator = "\n"),
                    environmentVariables = testCaseDTO.environment!!.joinToString(";") { "${it.key}=${it.value}" },
                )
            logger.info("Saving updated test case")
            testCaseRepository.save(updatedTestCase)
            return testCaseDTO
        } else {
            logger.error("Test case not found")
            throw SnippetNotFoundError("Test case not found")
        }
    }

    fun deleteTestCase(testCaseId: UUID) {
        logger.info("Deleting test case $testCaseId")
        testCaseRepository.deleteById(testCaseId)
    }

    fun runTestCase(
        testCaseId: UUID,
        userId: String,
    ): TestCaseResultDTO {
        logger.info("Running test case $testCaseId")
        val testCase = findTestCaseById(testCaseId)
        if (!canExecuteTestCase(testCase, userId)) {
            logger.error("User does not have permission to run this test case")
            throw SnippetPermissionError("User does not have permission to run this test case")
        }
        val inputs = parseInputs(testCase.inputs)
        val expectedOutputs = parseOutputs(testCase.outputs)
        val environmentVariables = parseEnvironmentVariables(testCase.environmentVariables)
        logger.info("Getting snippet content for test case $testCaseId")
        val snippetContent = getSnippetContent(testCase.snippet!!.id!!)

        val executorInput =
            ExecutorInput(
                content = snippetContent,
                inputs = inputs,
                env = environmentVariables,
            )
        logger.info("Executing test case $testCaseId")
        val executorOutput = runnerService.execute(executorInput)
        logger.info("Test case executed  $testCaseId")
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
