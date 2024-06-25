package ingsis.group12.snippetoperations.runner.service

import ingsis.group12.snippetoperations.runner.input.ExecutorInput
import ingsis.group12.snippetoperations.runner.input.FormatterInput
import ingsis.group12.snippetoperations.runner.input.LinterInput
import ingsis.group12.snippetoperations.runner.output.ExecutorOutput
import ingsis.group12.snippetoperations.runner.output.FormatterOutput
import ingsis.group12.snippetoperations.runner.output.LinterOutput

interface RunnerService {
    fun execute(input: ExecutorInput): ExecutorOutput

    fun format(input: FormatterInput): FormatterOutput

    fun analyze(input: LinterInput): LinterOutput
}
