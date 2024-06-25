package ingsis.group12.snippetoperations.rule.controller

import ingsis.group12.snippetoperations.rule.dto.FormatterRules
import ingsis.group12.snippetoperations.rule.dto.LinterRuleInput
import ingsis.group12.snippetoperations.rule.dto.RunRuleDTO
import ingsis.group12.snippetoperations.runner.output.FormatterOutput
import ingsis.group12.snippetoperations.runner.output.LinterOutput
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
                content = [Content(schema = Schema(implementation = LinterRuleInput::class))],
            ),
        ],
    )
    fun createOrGetLinterRules(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<LinterRuleInput>

    @PutMapping("/linter")
    @Operation(
        summary = "Update a linter rule",
        security = [SecurityRequirement(name = "Bearer Token")],
        requestBody = RequestBodyDoc(content = [Content(schema = Schema(implementation = LinterRuleInput::class, required = true))]),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = LinterRuleInput::class))],
            ),
        ],
    )
    fun updateLinterRules(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody linterRuleInput: LinterRuleInput,
    ): ResponseEntity<LinterRuleInput>

    @PostMapping("/linter/run")
    @Operation(
        summary = "Run a linter rule",
        security = [SecurityRequirement(name = " Bearer Token")],
        requestBody = RequestBodyDoc(content = [Content(schema = Schema(implementation = RunRuleDTO::class, required = true))]),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = LinterOutput::class))],
            ),
        ],
    )
    fun runLinterRules(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody runRuleDTO: RunRuleDTO,
    ): ResponseEntity<LinterOutput>

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

    @PostMapping("/formatter/run")
    @Operation(
        summary = "Run a formatter rule",
        security = [SecurityRequirement(name = "Bearer Token")],
        requestBody = RequestBodyDoc(content = [Content(schema = Schema(implementation = RunRuleDTO::class, required = true))]),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = FormatterOutput::class))],
            ),
        ],
    )
    fun runFormatterRules(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody runRuleDTO: RunRuleDTO,
    ): ResponseEntity<FormatterOutput>
}
