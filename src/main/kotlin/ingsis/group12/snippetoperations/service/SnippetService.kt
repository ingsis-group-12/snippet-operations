package ingsis.group12.snippetoperations.service

import ingsis.group12.snippetoperations.input.SnippetInput

interface SnippetService {
    fun createSnippet(
        snippetInput: SnippetInput,
        userId: String,
    )
}
