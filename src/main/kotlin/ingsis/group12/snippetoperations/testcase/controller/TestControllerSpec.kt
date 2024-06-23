package ingsis.group12.snippetoperations.testcase.controller

import ingsis.group12.snippetoperations.testcase.dto.TestCaseDTO
import ingsis.group12.snippetoperations.testcase.dto.TestCaseResponseDTO
import ingsis.group12.snippetoperations.testcase.dto.TestCaseResultDTO
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

@RequestMapping("/test")
interface TestControllerSpec {
    @PostMapping("/{snippetId}")
    @Operation(
        summary = "Create a test case",
        requestBody = RequestBodyDoc(content = [Content(schema = Schema(implementation = TestCaseDTO::class, required = true))]),
        security = [SecurityRequirement(name = "Bearer Token")],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = TestCaseResponseDTO::class))],
            ),
            ApiResponse(responseCode = "409", description = "Conflict"),
        ],
        parameters = [
            Parameter(
                name = "snippetId",
                required = true,
                description = "Snippet ID",
                example = "123e4567-e89b-12d3-a456-426614174000",
            ),
        ],
    )
    fun createTestCase(
        @Valid @RequestBody testCaseDTO: TestCaseDTO,
        @PathVariable("snippetId") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<TestCaseResponseDTO>

    @GetMapping("/{snippetId}")
    @Operation(
        summary = "Get all test cases by snippet ID",
        security = [SecurityRequirement(name = "Bearer Token")],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = TestCaseResponseDTO::class))],
            ),
        ],
        parameters = [
            Parameter(
                name = "snippetId",
                required = true,
                description = "Snippet ID",
                example = "123e4567-e89b-12d3-a456-426614174000",
            ),
        ],
    )
    fun getTestCasesBySnippetId(
        @PathVariable("snippetId") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<List<TestCaseResponseDTO>>

    @PutMapping("/{testCaseId}")
    @Operation(
        summary = "Update a test case",
        requestBody = RequestBodyDoc(content = [Content(schema = Schema(implementation = TestCaseDTO::class, required = true))]),
        security = [SecurityRequirement(name = "Bearer Token")],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = TestCaseDTO::class))],
            ),
            ApiResponse(responseCode = "404", description = "Not Found when test case not found"),
            ApiResponse(responseCode = "409", description = "Conflict when error while updating test case"),
        ],
        parameters = [
            Parameter(
                name = "testCaseId",
                required = true,
                description = "Test Case ID",
                example = "123e4567-e89b-12d3-a456-426614174000",
            ),
        ],
    )
    fun updateTestCase(
        @Valid @RequestBody testCaseDTO: TestCaseDTO,
        @PathVariable("testCaseId") testCaseId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<TestCaseDTO>

    @DeleteMapping("/{testCaseId}")
    @Operation(
        summary = "Delete a test case by ID",
        security = [SecurityRequirement(name = "Bearer Token")],
        responses = [
            ApiResponse(responseCode = "204", description = "No Content"),
            ApiResponse(responseCode = "404", description = "Not Found when test case not found"),
            ApiResponse(responseCode = "409", description = "Conflict when error while deleting test case"),
        ],
        parameters = [
            Parameter(
                name = "testCaseId",
                required = true,
                description = "Test Case ID",
                example = "123e4567-e89b-12d3-a456-426614174000",
            ),
        ],
    )
    fun deleteTestCase(
        @PathVariable("testCaseId") testCaseId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Void>

    @PostMapping("/run/{testCaseId}")
    @Operation(
        summary = "Run a test case",
        security = [SecurityRequirement(name = "Bearer Token")],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = TestCaseResultDTO::class))],
            ),
            ApiResponse(responseCode = "404", description = "Not Found when test case not found"),
        ],
        parameters = [
            Parameter(
                name = "testCaseId",
                required = true,
                description = "Test Case ID",
                example = "123e4567-e89b-12d3-a456-426614174000",
            ),
        ],
    )
    fun runTestCase(
        @PathVariable("testCaseId") testCaseId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<TestCaseResultDTO>
}
