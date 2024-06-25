package ingsis.group12.snippetoperations.runner.input

import ingsis.group12.snippetoperations.testcase.dto.EnvironmentInput

data class ExecutorInput(
    val content: String,
    val language: String? = "printscript 1.1",
    val inputs: List<String>? = emptyList(),
    val env: List<EnvironmentInput>? = emptyList(),
)
