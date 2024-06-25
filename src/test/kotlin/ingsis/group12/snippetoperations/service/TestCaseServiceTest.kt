package ingsis.group12.snippetoperations.service

import ingsis.group12.snippetoperations.asset.model.Snippet
import ingsis.group12.snippetoperations.asset.repository.SnippetRepository
import ingsis.group12.snippetoperations.bucket.ObjectStoreService
import ingsis.group12.snippetoperations.mock.MockObjectStoreService
import ingsis.group12.snippetoperations.mock.MockPermissionService
import ingsis.group12.snippetoperations.mock.MockRunnerService
import ingsis.group12.snippetoperations.testcase.dto.EnvironmentInput
import ingsis.group12.snippetoperations.testcase.dto.TestCaseDTO
import ingsis.group12.snippetoperations.testcase.model.TestCase
import ingsis.group12.snippetoperations.testcase.repository.TestCaseRepository
import ingsis.group12.snippetoperations.testcase.service.TestCaseService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate
import java.util.Optional
import java.util.UUID

@SpringBootTest
@ExtendWith(SpringExtension::class)
class TestCaseServiceTest {
    @Autowired
    private lateinit var restTemplate: RestTemplate

    private val testCaseRepository: TestCaseRepository = mock(TestCaseRepository::class.java)

    private val objectStoreService: ObjectStoreService = MockObjectStoreService()

    private lateinit var mockServer: MockRestServiceServer

    private val snippetRepository: SnippetRepository = mock(SnippetRepository::class.java)

    private lateinit var testCaseService: TestCaseService

    @BeforeEach
    fun setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
        testCaseService =
            TestCaseService(
                testCaseRepository,
                snippetRepository,
                objectStoreService,
                MockRunnerService(),
                MockPermissionService(),
            )
    }

    @Test
    fun `create a test case should return a test case response`() {
        // Given
        val snippetId = UUID.randomUUID()
        val testCaseDTO =
            TestCaseDTO(
                "test",
                listOf("input1", "input2"),
                listOf("output1", "output2"),
                environment = listOf(EnvironmentInput("key", "value"), EnvironmentInput("key2", "value2")),
            )

        val snippet = Snippet(id = snippetId, name = "test", language = "java", extension = ".java")

        val testCase =
            TestCase(
                snippet = snippet,
                name = "test",
                inputs = "input1\ninput2",
                outputs = "output1\noutput2",
                environmentVariables = "key=value;key2=value2",
            )

        `when`(snippetRepository.findById(any())).thenReturn(Optional.of(snippet))
        `when`(testCaseRepository.save(any())).thenReturn(testCase)
        assertDoesNotThrow {
            testCaseService.createTestCase(snippetId, testCaseDTO)
        }
    }

    @Test
    fun `get test cases by snippet id should return a list of test case response`() {
        // Given
        val snippetId = UUID.randomUUID()
        val testCaseId = UUID.randomUUID()
        val snippet = Snippet(id = snippetId, name = "test", language = "java", extension = ".java")
        val testCase =
            TestCase(
                testCaseId,
                snippet = snippet,
                name = "test",
                inputs = "input1\ninput2",
                outputs = "output1\noutput2",
                environmentVariables = "key=value;key2=value2",
            )
        `when`(testCaseRepository.getTestCasesBySnippetId(snippetId)).thenReturn(listOf(testCase))
        val response = testCaseService.getTestCasesBySnippetId(snippetId)
        assert(response.isNotEmpty())
        Assertions.assertEquals(response[0].id, testCaseId)
        Assertions.assertEquals(response[0].name, "test")
        Assertions.assertEquals(response[0].inputs, listOf("input1", "input2"))
        Assertions.assertEquals(response[0].outputs, listOf("output1", "output2"))
        Assertions.assertEquals(response[0].environment, listOf(EnvironmentInput("key", "value"), EnvironmentInput("key2", "value2")))
    }

    @Test
    fun `update test case should return a test case dto`() {
        val testCaseId = UUID.randomUUID()
        val snippetId = UUID.randomUUID()
        val testCaseDTO =
            TestCaseDTO(
                "test update",
                listOf("input1", "input2"),
                listOf("output1", "output2"),
                environment = listOf(EnvironmentInput("key", "value"), EnvironmentInput("key2", "value2")),
            )

        val snippet = Snippet(id = snippetId, name = "test", language = "java", extension = ".java")

        val testCase =
            TestCase(
                testCaseId,
                snippet = snippet,
                name = "test",
                inputs = "input1\ninput2",
                outputs = "output1\noutput2",
                environmentVariables = "key=value;key2=value2",
            )

        `when`(testCaseRepository.findById(any())).thenReturn(Optional.of(testCase))
        `when`(testCaseRepository.save(any())).thenReturn(testCase)
        val response = testCaseService.updateTestCase(testCaseId, testCaseDTO)
        Assertions.assertEquals(response.name, "test update")
        Assertions.assertEquals(response.inputs, listOf("input1", "input2"))
        Assertions.assertEquals(response.outputs, listOf("output1", "output2"))
        Assertions.assertEquals(response.environment, listOf(EnvironmentInput("key", "value"), EnvironmentInput("key2", "value2")))
    }

    @Test
    fun `delete test case should not throw an exception`() {
        val testCaseId = UUID.randomUUID()
        assertDoesNotThrow {
            testCaseService.deleteTestCase(testCaseId)
        }
    }
}
