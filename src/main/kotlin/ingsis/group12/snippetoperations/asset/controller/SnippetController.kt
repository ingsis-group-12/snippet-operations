package ingsis.group12.snippetoperations.asset.controller

import ingsis.group12.snippetoperations.asset.dto.ShareDTO
import ingsis.group12.snippetoperations.asset.dto.SnippetDTO
import ingsis.group12.snippetoperations.asset.input.SnippetInput
import ingsis.group12.snippetoperations.asset.input.SnippetUpdateInput
import ingsis.group12.snippetoperations.asset.service.SnippetService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/snippet")
@Tag(name = "Snippet")
@CrossOrigin(origins = ["*"])
class SnippetController(
    private val snippetService: SnippetService,
) : SnippetControllerSpec {
    @PostMapping()
    override fun createSnippet(
        @Valid @RequestBody snippetInput: SnippetInput,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO> {
        val userId = jwt.subject
        val snippet = snippetService.createAsset(snippetInput, userId)
        return ResponseEntity.ok(snippet)
    }

    @GetMapping()
    override fun getSnippets(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<List<SnippetDTO>> {
        val userId = jwt.subject
        return ResponseEntity.ok(snippetService.getAssets(userId))
    }

    @GetMapping("/{id}")
    override fun getSnippetById(
        @PathVariable("id") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO> {
        val result = snippetService.getAssetById(snippetId)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/{id}")
    override fun deleteSnippetById(
        @PathVariable("id") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Void> {
        val userId = jwt.subject
        snippetService.deleteAssetById(snippetId, userId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/share")
    override fun shareSnippet(
        @Valid @RequestBody shareDTO: ShareDTO,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Void> {
        val userId = jwt.subject
        snippetService.shareAsset(userId, shareDTO)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    override fun updateSnippet(
        @PathVariable("id")snippetId: UUID,
        @Valid @RequestBody input: SnippetUpdateInput,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO> {
        val userId = jwt.subject
        val snippet = snippetService.updateAsset(snippetId, input, userId)
        return ResponseEntity.ok(snippet)
    }
}
