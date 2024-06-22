package ingsis.group12.snippetoperations.testcase.dto

import jakarta.validation.constraints.NotNull

data class TestCaseDTO(
    @field:NotNull(message = "name of test case is missing")
    val name: String,
    @field:NotNull(message = "inputs are missing")
    val inputs: List<String>?,
    @field:NotNull(message = "outputs are missing")
    val outputs: List<String>?,
    val environment: List<EnvironmentInput>? = emptyList(),
)

data class EnvironmentInput(
    val key: String,
    val value: String,
)
