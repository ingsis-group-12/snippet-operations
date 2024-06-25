package ingsis.group12.snippetoperations.runner.input

data class LinterInput(
    val content: String,
    val language: String? = "printscript 1.1",
    val rules: String,
)
