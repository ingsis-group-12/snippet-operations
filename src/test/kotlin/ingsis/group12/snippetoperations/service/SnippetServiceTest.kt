package ingsis.group12.snippetoperations.service

import ingsis.group12.snippetoperations.asset.dto.ShareDTO
import ingsis.group12.snippetoperations.asset.input.SnippetInput
import ingsis.group12.snippetoperations.asset.input.SnippetUpdateInput
import ingsis.group12.snippetoperations.asset.model.Snippet
import ingsis.group12.snippetoperations.asset.repository.SnippetRepository
import ingsis.group12.snippetoperations.asset.service.SnippetService
import ingsis.group12.snippetoperations.bucket.ObjectStoreService
import ingsis.group12.snippetoperations.exception.SnippetCreationError
import ingsis.group12.snippetoperations.exception.SnippetNotFoundError
import ingsis.group12.snippetoperations.mock.MockLinterRuleService
import ingsis.group12.snippetoperations.mock.MockObjectStoreService
import ingsis.group12.snippetoperations.mock.MockObjectStoreServiceWithConflict
import ingsis.group12.snippetoperations.mock.MockPermissionService
import ingsis.group12.snippetoperations.mock.MockPermissionServiceAsNotOwner
import ingsis.group12.snippetoperations.mock.MockPermissionServiceWithBadResponse
import ingsis.group12.snippetoperations.mock.MockRunnerService
import ingsis.group12.snippetoperations.permission.service.PermissionService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.Optional
import java.util.UUID

@SpringBootTest
@ExtendWith(SpringExtension::class)
class SnippetServiceTest {
    private val snippetRepository: SnippetRepository = mock(SnippetRepository::class.java)

    private val objectStoreService: ObjectStoreService = MockObjectStoreService()

    private val permissionService: PermissionService = MockPermissionService()

    private lateinit var snippetService: SnippetService

    @BeforeEach
    fun setUp() {
        snippetService =
            SnippetService(
                snippetRepository,
                objectStoreService,
                permissionService,
                MockRunnerService(),
                MockLinterRuleService(),
            )
    }

    @Test
    fun `createAsset should create a new snippet when permissions and storage are successful`() {
        val snippetInput = SnippetInput("test", "content", "java", ".java", "userName")
        val userId = "user1"
        `when`(snippetRepository.save(any())).thenReturn(Snippet(UUID.randomUUID(), "test", "java", ".java"))
        val result = snippetService.createAsset(snippetInput, userId)

        assertNotNull(result.id)
        assertEquals(snippetInput.name, result.name)
        assertEquals(snippetInput.content, result.content)
        assertEquals(snippetInput.language, result.language)
        assertEquals(snippetInput.extension, result.extension)
    }

    @Test
    fun `getAssetById should return a snippet when it exists`() {
        val snippetId = UUID.randomUUID()
        val snippet = Snippet(snippetId, "test", "java", ".java")

        `when`(snippetRepository.findById(any())).thenReturn(Optional.of(snippet))

        val result = snippetService.getAssetById(snippetId)

        assertEquals(snippetId, result.id)
        assertEquals(snippet.name, result.name)
        assertEquals("let a : number = 5;", result.content)
        assertEquals(snippet.language, result.language)
        assertEquals(snippet.extension, result.extension)
    }

    @Test
    fun `getAssets by user id should return all snippets`() {
        val userId = "user1"
        val snippet1 = Snippet(UUID.randomUUID(), "test1", "java", ".java")
        val snippet2 = Snippet(UUID.randomUUID(), "test2", "kotlin", ".kt")

        `when`(snippetRepository.findById(any())).thenReturn(Optional.of(snippet1))
        `when`(snippetRepository.findById(any())).thenReturn(Optional.of(snippet2))
        val result = snippetService.getAssets(userId)

        assertEquals(2, result.size)
    }

    @Test
    fun `deleteAssetById should delete a snippet when it exists and storage deletion is successful`() {
        val snippetId = UUID.randomUUID()
        val snippet = Snippet(snippetId, "test", "java", ".java")

        `when`(snippetRepository.findById(any())).thenReturn(Optional.of(snippet))

        val result = snippetService.deleteAssetById(snippetId, "user1")

        assertEquals("Snippet deleted with id $snippetId", result)
    }

    @Test
    fun `createAsset should throw an error when permissions creation fails`() {
        val snippetInput = SnippetInput("test", "content", "java", ".java", "userName")
        val userId = "user1"
        snippetService =
            SnippetService(
                snippetRepository, objectStoreService,
                MockPermissionServiceWithBadResponse(), MockRunnerService(), MockLinterRuleService(),
            )
        assertThrows<SnippetCreationError> {
            snippetService.createAsset(snippetInput, userId)
        }
    }

    @Test
    fun `createAsset should throw an error when storage creation fails`() {
        val snippetInput = SnippetInput("test", "content", "java", ".java", "userName")
        val userId = "user1"

        snippetService =
            SnippetService(
                snippetRepository, MockObjectStoreServiceWithConflict(),
                permissionService, MockRunnerService(), MockLinterRuleService(),
            )
        assertThrows<SnippetCreationError> {
            snippetService.createAsset(snippetInput, userId)
        }
    }

    @Test
    fun `getAssetById should throw an error when snippet does not exist`() {
        val snippetId = UUID.randomUUID()

        `when`(snippetRepository.findById(any())).thenReturn(Optional.empty())

        assertThrows<SnippetNotFoundError> {
            snippetService.getAssetById(snippetId)
        }
    }

    @Test
    fun `deleteAssetById should throw an error when snippet does not exist`() {
        val snippetId = UUID.randomUUID()

        `when`(snippetRepository.findById(any())).thenReturn(Optional.empty())

        assertThrows<SnippetNotFoundError> {
            snippetService.deleteAssetById(snippetId, "user1")
        }
    }

    @Test
    fun `deleteAssetById should throw an error when storage deletion fails`() {
        val snippetId = UUID.randomUUID()
        val userId = "user1"
        snippetService =
            SnippetService(
                snippetRepository, MockObjectStoreServiceWithConflict(),
                permissionService, MockRunnerService(), MockLinterRuleService(),
            )

        assertThrows<SnippetNotFoundError> {
            snippetService.deleteAssetById(snippetId, userId)
        }
    }

    @Test
    fun `deleteAssetById as not owner should fail`() {
        snippetService =
            SnippetService(
                snippetRepository, objectStoreService,
                MockPermissionServiceAsNotOwner(), MockRunnerService(), MockLinterRuleService(),
            )
        val snippetId = UUID.randomUUID()
        val snippet = Snippet(snippetId, "test", "java", ".java")

        `when`(snippetRepository.findById(any())).thenReturn(Optional.of(snippet))

        assertThrows<Exception> {
            snippetService.deleteAssetById(snippetId, "user2")
        }
    }

    @Test
    fun `shareAsset should share a snippet when it exists`() {
        val userId = "user1"
        val shareDTO = ShareDTO(UUID.randomUUID(), userId, "userName")
        val snippetId = UUID.randomUUID()
        val snippet = Snippet(snippetId, "test", "java", ".java")

        `when`(snippetRepository.findById(any())).thenReturn(Optional.of(snippet))
        assertDoesNotThrow { snippetService.shareAsset(userId, shareDTO) }
    }

    @Test
    fun `shareAsset should throw an error when snippet does not exist`() {
        val userId = "user1"
        val shareDTO = ShareDTO(UUID.randomUUID(), userId, "userName")

        `when`(snippetRepository.findById(any())).thenReturn(Optional.empty())

        assertThrows<SnippetNotFoundError> {
            snippetService.shareAsset(userId, shareDTO)
        }
    }

    @Test
    fun `shareAsset should fail when user is not owner of the asset`() {
        snippetService =
            SnippetService(
                snippetRepository, objectStoreService,
                MockPermissionServiceAsNotOwner(), MockRunnerService(), MockLinterRuleService(),
            )
        val userId = "user2"
        val shareDTO = ShareDTO(UUID.randomUUID(), userId, "userName")
        val snippetId = UUID.randomUUID()
        val snippet = Snippet(snippetId, "test", "java", ".java")

        `when`(snippetRepository.findById(any())).thenReturn(Optional.of(snippet))

        assertThrows<Exception> {
            snippetService.shareAsset(userId, shareDTO)
        }
    }

    @Test
    fun `updateAsset should update a snippet when permissions and storage are successful`() {
        val userId = "user1"
        val snippetId = UUID.randomUUID()
        val snippet = Snippet(snippetId, "test", "java", ".java")
        val input = SnippetUpdateInput("test", "content 2 ")
        `when`(snippetRepository.findById(snippetId)).thenReturn(Optional.of(snippet))
        val result = snippetService.updateAsset(snippetId, input, userId)
        assertNotNull(result.id)
        assertEquals(snippet.name, result.name)
        assertEquals(input.content, result.content)
        assertEquals(snippet.language, result.language)
        assertEquals(snippet.extension, result.extension)
    }

    @Test
    fun `updateAsset should throw an error when permissions creation fails`() {
        val userId = "user1"
        val snippetId = UUID.randomUUID()
        val input = SnippetUpdateInput("test", "content 2 ")
        `when`(snippetRepository.findById(snippetId)).thenReturn(Optional.empty())
        snippetService =
            SnippetService(
                snippetRepository, objectStoreService, MockPermissionService(),
                MockRunnerService(), MockLinterRuleService(),
            )
        assertThrows<SnippetNotFoundError> {
            snippetService.updateAsset(snippetId, input, userId)
        }
    }
}
