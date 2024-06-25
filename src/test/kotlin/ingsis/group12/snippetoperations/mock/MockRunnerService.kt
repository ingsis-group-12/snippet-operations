package ingsis.group12.snippetoperations.mock

import ingsis.group12.snippetoperations.runner.input.ExecutorInput
import ingsis.group12.snippetoperations.runner.input.FormatterInput
import ingsis.group12.snippetoperations.runner.input.LinterInput
import ingsis.group12.snippetoperations.runner.output.ExecutorOutput
import ingsis.group12.snippetoperations.runner.output.FormatterOutput
import ingsis.group12.snippetoperations.runner.output.LinterOutput
import ingsis.group12.snippetoperations.runner.service.RunnerService

class MockRunnerService : RunnerService {
    override fun execute(input: ExecutorInput): ExecutorOutput {
        return ExecutorOutput(listOf(), "")
    }

    override fun format(input: FormatterInput): FormatterOutput {
        return FormatterOutput("", "")
    }

    override fun analyze(input: LinterInput): LinterOutput {
        return LinterOutput("", "")
    }
}
