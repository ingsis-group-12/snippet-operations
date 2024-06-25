package ingsis.group12.snippetoperations.testcase.controller
import ingsis.group12.snippetoperations.testcase.dto.TestCaseDTO
import ingsis.group12.snippetoperations.testcase.dto.TestCaseResponseDTO
import ingsis.group12.snippetoperations.testcase.dto.TestCaseResultDTO
import ingsis.group12.snippetoperations.testcase.service.TestCaseService
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
@RequestMapping("/test")
@Tag(name = "Test")
@CrossOrigin("*")
class TestController(
    private val testCaseService: TestCaseService,
) {
    @PostMapping("/{snippetId}")
    fun createTestCase(
        @Valid @RequestBody testCaseDTO: TestCaseDTO,
        @PathVariable("snippetId") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<TestCaseResponseDTO> {
        val response = testCaseService.createTestCase(snippetId, testCaseDTO)
        return ResponseEntity.ok().body(response)
    }

    @GetMapping("/{snippetId}")
    fun getTestCasesBySnippetId(
        @PathVariable("snippetId") snippetId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<List<TestCaseResponseDTO>> {
        val response = testCaseService.getTestCasesBySnippetId(snippetId)
        return ResponseEntity.ok().body(response)
    }

    @PutMapping("/{testCaseId}")
    fun updateTestCase(
        @Valid @RequestBody testCaseDTO: TestCaseDTO,
        @PathVariable("testCaseId") testCaseId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<TestCaseDTO> {
        val response = testCaseService.updateTestCase(testCaseId, testCaseDTO)
        return ResponseEntity.ok().body(response)
    }

    @DeleteMapping("/{testCaseId}")
    fun deleteTestCase(
        @PathVariable("testCaseId") testCaseId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<String> {
        testCaseService.deleteTestCase(testCaseId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/run/{testCaseId}")
    fun runTestCase(
        @PathVariable("testCaseId") testCaseId: UUID,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<TestCaseResultDTO> {
        val userId = jwt.subject
        val response = testCaseService.runTestCase(testCaseId, userId)
        return ResponseEntity.ok().body(response)
    }
}
