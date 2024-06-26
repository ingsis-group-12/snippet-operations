package ingsis.group12.snippetoperations.asset.controller

import ingsis.group12.snippetoperations.asset.dto.ShareDTO
import ingsis.group12.snippetoperations.asset.dto.SnippetDTO
import ingsis.group12.snippetoperations.asset.dto.UserShareDTO
import ingsis.group12.snippetoperations.asset.input.SnippetInput
import ingsis.group12.snippetoperations.asset.input.SnippetUpdateInput
import ingsis.group12.snippetoperations.runner.input.ExecutorInput
import ingsis.group12.snippetoperations.runner.output.ExecutorOutput
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID
import io.swagger.v3.oas.annotations.parameters.RequestBody as RequestBodyDoc

@RequestMapping("/snippet")
interface SnippetControllerSpec {
    @PostMapping()
    @Operation(
        summary = "Create a snippet",
        requestBody = RequestBodyDoc(content = [Content(schema = Schema(implementation = SnippetInput::class, required = true))]),
        security = [SecurityRequirement(name = "Bearer Token")],
        responses = [
            ApiResponse(responseCode = "200", description = "OK", content = [Content(schema = Schema(implementation = SnippetDTO::class))]),
            ApiResponse(responseCode = "409", description = "Conflict"),
        ],
    )
    fun createSnippet(
        @Valid @RequestBody snippetInput: SnippetInput,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO>

    @GetMapping()
    @Operation(
        summary = "Get all snippets where user has access to",
        security = [SecurityRequirement(name = "Bearer Token")],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = arrayOf(Content(schema = Schema(implementation = SnippetDTO::class))),
            ),
        ],
    )
    fun getSnippets(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<List<SnippetDTO>>

    @GetMapping("/{id}")
    @Operation(
        summary = "Get a snippet by id",
        security = [SecurityRequirement(name = "Bearer Token")],
        parameters = [
            Parameter(
                name = "snippetId",
                required = true,
                description = "Snippet id",
                example = "123e4567-e89b-12d3-a456-426614174000",
            ),
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = SnippetDTO::class))],
            ),
        ],
    )
    fun getSnippetById(
        @PathVariable("id") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO>

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a snippet by id",
        security = [SecurityRequirement(name = "Bearer Token")],
        parameters = [
            Parameter(
                name = "id",
                required = true,
                description = "Snippet id",
                example = "123e4567-e89b-12d3-a456-426614174000",
            ),
        ],
        responses = [
            ApiResponse(
                responseCode = "204",
            ), ApiResponse(
                responseCode = "404",
                description = "Not Found when snippet not found",
            ), ApiResponse(responseCode = "409", description = "Conflict when error while deleting snippet"),
        ],
    )
    fun deleteSnippetById(
        @PathVariable("id") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Void>

    @PostMapping("/share")
    @Operation(
        summary = "Share a snippet",
        security = [SecurityRequirement(name = "Bearer Token")],
        requestBody = RequestBodyDoc(content = [Content(schema = Schema(implementation = ShareDTO::class, required = true))]),
        responses = [
            ApiResponse(
                responseCode = "204",
                description = "OK",
            ), ApiResponse(
                responseCode = "404",
                description = "Not Found when snippet not found",
            ), ApiResponse(responseCode = "409", description = "Conflict when error while sharing snippet"),
        ],
    )
    fun shareSnippet(
        @RequestBody shareDTO: ShareDTO,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Void>

    @PutMapping("/{id}")
    @Operation(
        summary = "Update a snippet",
        security = [SecurityRequirement(name = " Bearer Token")],
        parameters = [
            Parameter(
                name = "id",
                required = true,
                description = "Snippet id",
                example = "123e4567-e89b-12d3-a456-426614174000",
            ),
        ],
        requestBody = RequestBodyDoc(content = [Content(schema = Schema(implementation = SnippetUpdateInput::class, required = true))]),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = SnippetDTO::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not Found when snippet not found",
            ),
            ApiResponse(
                responseCode = "409",
                description = "Conflict when error while updating snippet",
            ),
        ],
    )
    fun updateSnippet(
        @PathVariable("id") snippetId: UUID,
        @RequestBody input: SnippetUpdateInput,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<SnippetDTO>

    @GetMapping("/share/{id}")
    @Operation(
        summary = "Get users to share a snippet with",
        security = [SecurityRequirement(name = "Bearer Token")],
        parameters = [
            Parameter(
                name = "id",
                required = true,
                description = "Snippet id",
                example = "123e4567-e89b-12d3-a456-426614174000",
            ),
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = arrayOf(Content(schema = Schema(implementation = UserShareDTO::class))),
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not Found when snippet not found",
            ),
            ApiResponse(
                responseCode = "409",
                description = "Conflict when error while retrieving users",
            ),
        ],
    )
    fun getSharedSnippet(
        @PathVariable("id") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<List<UserShareDTO>>

    @PostMapping("/run/{snippetId}")
    @Operation(
        summary = "Run a snippet",
        security = [SecurityRequirement(name = " Bearer Token")],
        parameters = [
            Parameter(
                name = "snippetId",
                required = true,
                description = "Snippet id",
                example = "123e4567-e89b-12d3-a456-426614174000",
            ),
        ],
        requestBody =
            RequestBodyDoc(content = [Content(schema = Schema(implementation = ExecutorInput::class, required = true))]),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = ExecutorOutput::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "Not Found when snippet not found",
            ),
            ApiResponse(
                responseCode = "409",
                description = "User has not permissions to run snippet",
            ),
        ],
    )
    fun runSnippet(
        @PathVariable("snippetId") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody executeInput: ExecutorInput,
    ): ResponseEntity<ExecutorOutput>
}
