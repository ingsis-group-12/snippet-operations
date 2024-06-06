package ingsis.group12.snippetoperations.snippet.service

import ingsis.group12.snippetoperations.snippet.dto.SnippetDTO
import ingsis.group12.snippetoperations.snippet.input.SnippetInput
import ingsis.group12.snippetoperations.snippet.model.Snippet
import ingsis.group12.snippetoperations.snippet.repository.SnippetRepository
import org.springframework.stereotype.Service

@Service
class SnippetServiceImpl(
    private val snippetRepository: SnippetRepository,
) : SnippetService {
    override fun createSnippet(
        snippetInput: SnippetInput,
        userId: String,
    ): SnippetDTO {
        val snippetCreated =
            snippetRepository.save(
                Snippet(
                    name = snippetInput.name,
                    language = snippetInput.language,
                    extension = snippetInput.extension,
                ),
            )
        return SnippetDTO(
            snippetCreated.id.toString(),
            snippetInput.name,
            snippetInput.content,
            snippetInput.language,
            snippetInput.extension,
        )
    }
}
