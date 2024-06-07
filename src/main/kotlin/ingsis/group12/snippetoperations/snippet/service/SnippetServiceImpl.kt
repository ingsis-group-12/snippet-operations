package ingsis.group12.snippetoperations.snippet.service

import ingsis.group12.snippetoperations.exception.SnippetCreationError
import ingsis.group12.snippetoperations.exception.SnippetNotFoundError
import ingsis.group12.snippetoperations.snippet.dto.SnippetDTO
import ingsis.group12.snippetoperations.snippet.input.SnippetInput
import ingsis.group12.snippetoperations.snippet.model.Snippet
import ingsis.group12.snippetoperations.snippet.repository.SnippetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.UUID

@Service
class SnippetServiceImpl(
    private val snippetRepository: SnippetRepository,
    @Value
    ("\${bucket.url}") private val bucketUrl: String,
) : SnippetService {
    @Autowired
    private lateinit var restTemplate: RestTemplate

    override fun createSnippet(
        snippetInput: SnippetInput,
        userId: String,
    ): SnippetDTO {
        val snippetId = UUID.randomUUID()
        val url = "$bucketUrl/$snippetId"
        val status = addSnippetIntoBucket(snippetInput, url)
        /*
        Todo: add permission api call
         */
        if (status == HttpStatus.CREATED) {
            return saveSnippet(snippetInput, snippetId)
        }
        throw SnippetCreationError("Error while creating snippet")
    }

    override fun getSnippetById(snippetId: UUID): SnippetDTO {
        val result = snippetRepository.findById(snippetId)
        if (result.isPresent) {
            val snippet = result.get()
            val content = getSnippetFromBucket(snippetId).body!!
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

    private fun getSnippetFromBucket(snippetId: UUID): ResponseEntity<String> {
        val url = "$bucketUrl/$snippetId"
        val response = restTemplate.getForEntity(url, String::class.java)
        return response
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

    private fun addSnippetIntoBucket(
        snippet: SnippetInput,
        url: String,
    ): HttpStatusCode {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(snippet.content, headers)
        val response = restTemplate.postForEntity(url, entity, String::class.java)
        return response.statusCode
    }
}
