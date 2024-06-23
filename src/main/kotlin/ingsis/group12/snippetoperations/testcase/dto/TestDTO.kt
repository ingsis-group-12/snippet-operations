package ingsis.group12.snippetoperations.testcase.dto

import jakarta.validation.constraints.NotNull
import java.util.UUID

data class TestCaseDTO(
    @field:NotNull(message = "name of test case is missing")
    val name: String,
    @field:NotNull(message = "inputs are missing")
    val inputs: List<String>?,
    @field:NotNull(message = "outputs are missing")
    val outputs: List<String>?,
    val environment: List<EnvironmentInput>? = emptyList(),
)

data class TestCaseResponseDTO(
    val id: UUID,
    val name: String,
    val inputs: List<String>,
    val outputs: List<String>,
    val environment: List<EnvironmentInput>,
)

data class TestCaseResultDTO(
    val passed: Boolean,
    val error: String?,
)

data class EnvironmentInput(
    val key: String,
    val value: String,
)
