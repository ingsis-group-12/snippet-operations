package ingsis.group12.snippetoperations.testcase.service

import ingsis.group12.snippetoperations.asset.repository.SnippetRepository
import ingsis.group12.snippetoperations.exception.SnippetNotFoundError
import ingsis.group12.snippetoperations.testcase.dto.EnvironmentInput
import ingsis.group12.snippetoperations.testcase.dto.TestCaseDTO
import ingsis.group12.snippetoperations.testcase.dto.TestCaseResponseDTO
import ingsis.group12.snippetoperations.testcase.model.TestCase
import ingsis.group12.snippetoperations.testcase.repository.TestCaseRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TestCaseService(
    @Value("\${runner.url}") private val runnerUrl: String,
    private val testCaseRepository: TestCaseRepository,
    private val snippetRepository: SnippetRepository,
) {
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

//    fun runTestCase(testCaseId: UUID): ExecutorOutput{
//        val testCase= testCaseRepository.findById(testCaseId)
//        if(testCase.isPresent){
//            val testCaseToRun = testCase.get()
//            val inputs = testCaseToRun.inputs.split("\n")
//            val outputs = testCaseToRun.outputs.split("\n")
//            val environmentVariables = testCaseToRun.environmentVariables.split(";").map {
//                val (key, value) = it.split("=")
//                EnvironmentInput(key.trim(), value.trim())
//            }
//            val executeUrl= "$runnerUrl/interpret"
//            val response =
//        }
//        else{
//            throw SnippetNotFoundError("Test case not found")
//        }
//    }
    private fun convertToTestCaseResponse(testCase: TestCase) =
        TestCaseResponseDTO(
            id = testCase.id,
            testCase.name,
            testCase.inputs.split("\n"),
            testCase.outputs.split("\n"),
            testCase.environmentVariables.split(";").map {
                val (key, value) = it.split("=")
                EnvironmentInput(key.trim(), value.trim())
            },
        )
}
