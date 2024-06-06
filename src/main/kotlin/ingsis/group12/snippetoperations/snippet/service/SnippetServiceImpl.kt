package ingsis.group12.snippetoperations.snippet.service

import ingsis.group12.snippetoperations.snippet.input.SnippetInput
import org.springframework.stereotype.Service

@Service
class SnippetServiceImpl : SnippetService {
    override fun createSnippet(
        snippetInput: SnippetInput,
        userId: String,
    ) {
        TODO("Not yet implemented")
    }
}
