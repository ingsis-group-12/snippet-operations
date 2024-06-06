package ingsis.group12.snippetoperations.snippet.controller

import ingsis.group12.snippetoperations.snippet.dto.SnippetDTO
import ingsis.group12.snippetoperations.snippet.input.SnippetInput
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/snippet")
@Tag(name = "Snippet")
class SnippetController {
    @PostMapping()
    @ApiResponse(responseCode = "200", description = "OK")
    fun createSnippet(
        @Valid @RequestBody snippetInput: SnippetInput,
    ): ResponseEntity<SnippetDTO> {
        return ResponseEntity.ok(SnippetDTO("1", "SNIPPET", "const num:number = 5;", "printscript"))
    }

    @GetMapping()
    @ApiResponse(responseCode = "200", description = "OK")
    fun getSnippets(): ResponseEntity<List<SnippetDTO>> {
        return ResponseEntity.ok(listOf(SnippetDTO("1", "name", "content", "language")))
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "OK")
    fun getSnippetById(
        @PathVariable("id") snippetId: String,
    ): ResponseEntity<SnippetDTO> {
        return ResponseEntity.ok(SnippetDTO(snippetId, "name", "content", "language"))
    }

    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "OK")
    fun updateSnippet(
        @PathVariable("id") snippetId: String,
        @Valid @RequestBody snippetInput: SnippetInput,
    ): ResponseEntity<SnippetDTO> {
        return ResponseEntity.ok(SnippetDTO(snippetId, "name", "content", "language"))
    }
}
