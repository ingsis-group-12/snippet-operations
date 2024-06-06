package ingsis.group12.snippetoperations.snippet.service

import ingsis.group12.snippetoperations.snippet.dto.SnippetDTO
import ingsis.group12.snippetoperations.snippet.input.SnippetInput

interface SnippetService {
    fun createSnippet(
        snippetInput: SnippetInput,
        userId: String,
    ): SnippetDTO
}
