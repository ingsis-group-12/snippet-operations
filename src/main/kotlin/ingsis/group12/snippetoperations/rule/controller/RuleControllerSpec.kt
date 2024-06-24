package ingsis.group12.snippetoperations.rule.controller

import ingsis.group12.snippetoperations.rule.dto.FormatterRules
import ingsis.group12.snippetoperations.rule.dto.LinterRules
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import io.swagger.v3.oas.annotations.parameters.RequestBody as RequestBodyDoc

@RequestMapping("/rule")
interface RuleControllerSpec {
    @PostMapping("/linter")
    @Operation(
        summary = "Create or Get a linter rule",
        security = [SecurityRequirement(name = "Bearer Token")],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = LinterRules::class))],
            ),
        ],
    )
    fun createOrGetLinterRules(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<LinterRules>

    @PutMapping("/linter")
    @Operation(
        summary = "Update a linter rule",
        security = [SecurityRequirement(name = "Bearer Token")],
        requestBody = RequestBodyDoc(content = [Content(schema = Schema(implementation = LinterRules::class, required = true))]),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = LinterRules::class))],
            ),
        ],
    )
    fun updateLinterRules(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody linterRules: LinterRules,
    ): ResponseEntity<LinterRules>

    @PostMapping("/formatter")
    @Operation(
        summary = "Create or Get a formatter rule",
        security = [SecurityRequirement(name = "Bearer Token")],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = FormatterRules::class))],
            ),
        ],
    )
    fun createOrGetFormatterRules(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<FormatterRules>

    @PutMapping("/formatter")
    @Operation(
        summary = "Update a formatter rule",
        security = [SecurityRequirement(name = "Bearer Token")],
        requestBody = RequestBodyDoc(content = [Content(schema = Schema(implementation = FormatterRules::class, required = true))]),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = FormatterRules::class))],
            ),
        ],
    )
    fun updateFormatterRules(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody formatterRules: FormatterRules,
    ): ResponseEntity<FormatterRules>
}
