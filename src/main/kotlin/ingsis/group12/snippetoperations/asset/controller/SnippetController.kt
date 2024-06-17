package ingsis.group12.snippetoperations.asset.controller

import ingsis.group12.snippetoperations.asset.dto.ShareDTO
import ingsis.group12.snippetoperations.asset.dto.SnippetDTO
import ingsis.group12.snippetoperations.asset.input.SnippetInput
import ingsis.group12.snippetoperations.asset.service.SnippetService
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
    @PostMapping()
    @ApiResponse(responseCode = "200", description = "OK")
    fun createSnippet(
        @Valid @RequestBody snippetInput: SnippetInput,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO> {
        val userId = jwt.subject
        val snippet = snippetService.createAsset(snippetInput, userId)
        return ResponseEntity.ok(snippet)
    }

    @GetMapping()
    @ApiResponse(responseCode = "200", description = "OK")
    fun getSnippets(): ResponseEntity<List<SnippetDTO>> {
        return ResponseEntity.ok(snippetService.getAssets())
    }

    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "OK")
    fun getSnippetById(
        @PathVariable("id") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO> {
        val result = snippetService.getAssetById(snippetId)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "OK")
    fun deleteSnippetById(
        @PathVariable("id") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<String> {
        val result = snippetService.deleteAssetById(snippetId)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/share")
    @ApiResponse(responseCode = "204", description = "OK")
    fun shareSnippet(
        @Valid @RequestBody shareDTO: ShareDTO,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO> {
        val userId = jwt.subject
        snippetService.shareAsset(userId, shareDTO)
        return ResponseEntity.noContent().build()
    }
}
