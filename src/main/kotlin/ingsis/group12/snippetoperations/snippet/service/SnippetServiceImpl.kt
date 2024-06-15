package ingsis.group12.snippetoperations.snippet.service

import ingsis.group12.snippetoperations.bucket.ObjectStoreService
import ingsis.group12.snippetoperations.exception.SnippetCreationError
import ingsis.group12.snippetoperations.exception.SnippetDeleteError
import ingsis.group12.snippetoperations.exception.SnippetNotFoundError
import ingsis.group12.snippetoperations.snippet.dto.SnippetDTO
import ingsis.group12.snippetoperations.snippet.input.SnippetInput
import ingsis.group12.snippetoperations.snippet.model.Snippet
import ingsis.group12.snippetoperations.snippet.repository.SnippetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.UUID

@Service
class SnippetServiceImpl(
    private val snippetRepository: SnippetRepository,
    private val objectStoreService: ObjectStoreService,
) : SnippetService {
    @Autowired
    private lateinit var restTemplate: RestTemplate

    override fun createSnippet(
        snippetInput: SnippetInput,
        userId: String,
    ): SnippetDTO {
        val snippetId = UUID.randomUUID()
        val response = objectStoreService.create(snippetInput.content, snippetId)
        if (response.statusCode.is2xxSuccessful) {
            return saveSnippet(snippetInput, snippetId)
        }
        throw SnippetCreationError("Error while creating snippet")
    }

    override fun getSnippetById(snippetId: UUID): SnippetDTO {
        val result = snippetRepository.findById(snippetId)
        if (result.isPresent) {
            val snippet = result.get()
            val content = objectStoreService.get(snippetId).body!!
            return SnippetDTO(
                snippetId,
                snippet.name!!,
                content,
                snippet.language!!,
                snippet.extension!!,
            )
        }
        throw SnippetNotFoundError("Snippet not found")
    }

    override fun deleteSnippetById(snippetId: UUID): String {
        val result = snippetRepository.findById(snippetId)
        if (result.isPresent) {
            val response = objectStoreService.delete(snippetId)
            if (response.statusCode.is2xxSuccessful) {
                snippetRepository.deleteById(snippetId)
                return "Snippet deleted with id $snippetId"
            } else {
                throw SnippetDeleteError("Error while deleting snippet from bucket")
            }
        }
        throw SnippetNotFoundError("Snippet not found")
    }

    private fun saveSnippet(
        snippetInput: SnippetInput,
        snippetId: UUID,
    ): SnippetDTO {
        snippetRepository.save(
            Snippet(
                id = snippetId,
                name = snippetInput.name,
                language = snippetInput.language,
                extension = snippetInput.extension,
            ),
        )
        return SnippetDTO(
            snippetId,
            snippetInput.name,
            snippetInput.content,
            snippetInput.language,
            snippetInput.extension,
        )
    }
}
