package ingsis.group12.snippetoperations.controller

import ingsis.group12.snippetoperations.input.Snippet
import ingsis.group12.snippetoperations.model.SnippetDTO
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/snippet")
@Tag(name = "Snippet")
class SnippetController {
    /*
    To get the auth0 userId use Principal java security for each request that you need it.
    The code above is just an example to use it.
     */
    @GetMapping("/jwt")
    fun jwt(
        @RequestHeader("Authorization") token: String,
        principal: Principal,
    ): String {
        return principal.name
    }

    @PostMapping()
    @ApiResponse(responseCode = "200", description = "OK")
    fun createSnippet(
        @Valid @RequestBody snippet: Snippet,
    ): ResponseEntity<SnippetDTO> {
        return ResponseEntity.ok(SnippetDTO("1", "SNIPPET", "const num:number = 5;", "printscript", ".ps"))
    }

    @GetMapping()
    @ApiResponse(responseCode = "200", description = "OK")
    fun getSnippets(): ResponseEntity<List<SnippetDTO>> {
        return ResponseEntity.ok(listOf(SnippetDTO("1", "name", "content", "language", "extension")))
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "OK")
    fun getSnippetById(
        @PathVariable("id") snippetId: String,
    ): ResponseEntity<SnippetDTO> {
        return ResponseEntity.ok(SnippetDTO(snippetId, "name", "content", "language", "extension"))
    }

    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "OK")
    fun updateSnippet(
        @PathVariable("id") snippetId: String,
        @Valid @RequestBody snippet: Snippet,
    ): ResponseEntity<SnippetDTO> {
        return ResponseEntity.ok(SnippetDTO(snippetId, "name", "content", "language", "extension"))
    }
}
