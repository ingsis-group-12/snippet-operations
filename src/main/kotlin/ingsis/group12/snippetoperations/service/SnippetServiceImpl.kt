package ingsis.group12.snippetoperations.service

import ingsis.group12.snippetoperations.input.SnippetInput
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
