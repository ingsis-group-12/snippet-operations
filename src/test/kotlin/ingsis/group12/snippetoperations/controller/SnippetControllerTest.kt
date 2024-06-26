package ingsis.group12.snippetoperations.controller

import ingsis.group12.snippetoperations.asset.controller.SnippetController
import ingsis.group12.snippetoperations.asset.dto.SnippetDTO
import ingsis.group12.snippetoperations.asset.input.SnippetInput
import ingsis.group12.snippetoperations.asset.input.SnippetUpdateInput
import ingsis.group12.snippetoperations.asset.model.ComplianceType
import ingsis.group12.snippetoperations.asset.service.SnippetService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.test.annotation.DirtiesContext
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SnippetControllerTest {
    @Mock
    lateinit var snippetService: SnippetService

    @Mock
    lateinit var jwt: Jwt

    lateinit var snippetController: SnippetController

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        snippetController = SnippetController(snippetService)
    }

    @Test
    fun `createSnippet should return ResponseEntity with SnippetDTO when successful`() {
        val snippetInput = SnippetInput("title", "content", "language", "extension", "userName")
        val snippetDTO =
            SnippetDTO(
                UUID.randomUUID(),
                "title",
                "content",
                "language",
                "extension",
                "username",
                "userId",
                ComplianceType.COMPLIANT,
            )
        `when`(jwt.subject).thenReturn("userId")
        `when`(snippetService.createAsset(snippetInput, "userId")).thenReturn(snippetDTO)

        val result = snippetController.createSnippet(snippetInput, jwt)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(snippetDTO, result.body)
    }

    @Test
    fun `getSnippets should return ResponseEntity with list of SnippetDTO when successful`() {
        val userId = "userId"
        val snippetDTOList =
            listOf(
                SnippetDTO(
                    UUID.randomUUID(),
                    "title",
                    "content",
                    "language",
                    "extension",
                    "username",
                    "userId",
                    ComplianceType.COMPLIANT,
                ),
            )
        `when`(jwt.subject).thenReturn("userId")
        `when`(snippetService.getAssets(userId)).thenReturn(snippetDTOList)

        val result = snippetController.getSnippets(jwt)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(snippetDTOList, result.body)
    }

    @Test
    fun `getSnippetById should return ResponseEntity with SnippetDTO when successful`() {
        val snippetId = UUID.randomUUID()
        val snippetDTO =
            SnippetDTO(
                snippetId,
                "title",
                "content",
                "language",
                "extension",
                "username",
                "userId",
                ComplianceType.COMPLIANT,
            )
        `when`(snippetService.getAssetById(snippetId)).thenReturn(snippetDTO)

        val result = snippetController.getSnippetById(snippetId, jwt)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(snippetDTO, result.body)
    }

    @Test
    fun `deleteSnippetById should return ResponseEntity with string when successful`() {
        val snippetId = UUID.randomUUID()
        `when`(jwt.subject).thenReturn("userId")
        `when`(snippetService.deleteAssetById(snippetId, "userId")).thenReturn("Deleted")

        val result = snippetController.deleteSnippetById(snippetId, jwt)

        assertEquals(HttpStatus.NO_CONTENT, result.statusCode)
    }

    @Test
    fun `updateSnippet should return ResponseEntity with SnippetDTO when successful`() {
        val snippetId = UUID.randomUUID()
        val snippetUpdateInput = SnippetUpdateInput("title", "content")
        val snippetDTO =
            SnippetDTO(
                snippetId,
                "title",
                "content",
                "language",
                "extension",
                "username",
                "userId",
                ComplianceType.COMPLIANT,
            )
        `when`(jwt.subject).thenReturn("userId")
        `when`(snippetService.updateAsset(snippetId, snippetUpdateInput, "userId")).thenReturn(snippetDTO)

        val result = snippetController.updateSnippet(snippetId, snippetUpdateInput, jwt)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(snippetDTO, result.body)
    }
}
