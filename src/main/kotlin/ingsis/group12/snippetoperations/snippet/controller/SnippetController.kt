package ingsis.group12.snippetoperations.snippet.controller

import ingsis.group12.snippetoperations.snippet.dto.SnippetDTO
import ingsis.group12.snippetoperations.snippet.input.SnippetInput
import ingsis.group12.snippetoperations.snippet.service.SnippetService
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/snippet")
@Tag(name = "Snippet")
class SnippetController(
    private val snippetService: SnippetService,
) {
    /*
     ** Example to get auth0 id on jwt token
     */
    @GetMapping("/jwt")
    fun jwt(
        @AuthenticationPrincipal jwt: Jwt,
    ): String {
        return jwt.subject
    }

    @PostMapping()
    @ApiResponse(responseCode = "200", description = "OK")
    fun createSnippet(
        @Valid @RequestBody snippetInput: SnippetInput,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO> {
        val userId = jwt.subject
        val snippet = snippetService.createSnippet(snippetInput, userId)
        return ResponseEntity.ok(snippet)
    }

    @GetMapping()
    @ApiResponse(responseCode = "200", description = "OK")
    fun getSnippets(): ResponseEntity<List<SnippetDTO>> {
        return ResponseEntity.ok(listOf(SnippetDTO(UUID.randomUUID(), "name", "content", "language", "ps")))
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "OK")
    fun getSnippetById(
        @PathVariable("id") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO> {
        val result = snippetService.getSnippetById(snippetId)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "OK")
    fun deleteSnippetById(
        @PathVariable("id") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<String> {
        val result = snippetService.deleteSnippetById(snippetId)
        return ResponseEntity.ok(result)
    }
}
