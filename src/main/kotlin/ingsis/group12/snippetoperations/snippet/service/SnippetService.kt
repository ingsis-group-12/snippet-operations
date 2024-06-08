package ingsis.group12.snippetoperations.snippet.service

import ingsis.group12.snippetoperations.snippet.dto.SnippetDTO
import ingsis.group12.snippetoperations.snippet.input.SnippetInput
import java.util.UUID

interface SnippetService {
    fun createSnippet(
        snippetInput: SnippetInput,
        userId: String,
    ): SnippetDTO

    fun getSnippetById(snippetId: UUID): SnippetDTO

    fun deleteSnippetById(snippetId: UUID): String
}
