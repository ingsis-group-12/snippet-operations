package ingsis.group12.snippetoperations.runner.service

import ingsis.group12.snippetoperations.runner.input.ExecutorInput
import ingsis.group12.snippetoperations.runner.input.FormatterInput
import ingsis.group12.snippetoperations.runner.input.LinterInput
import ingsis.group12.snippetoperations.runner.output.ExecutorOutput
import ingsis.group12.snippetoperations.runner.output.FormatterOutput
import ingsis.group12.snippetoperations.runner.output.LinterOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class SnippetRunnerService(
    @Value("\${runner.url}") private val runnerUrl: String,
) : RunnerService {
    @Autowired
    private lateinit var restTemplate: RestTemplate

    override fun execute(input: ExecutorInput): ExecutorOutput {
        val executeUrl = "$runnerUrl/interpret"
        val response = restTemplate.postForEntity(executeUrl, input, ExecutorOutput::class.java)
        return response.body!!
    }

    override fun format(input: FormatterInput): FormatterOutput {
        val formatUrl = "$runnerUrl/format"
        val response = restTemplate.postForEntity(formatUrl, input, FormatterOutput::class.java)
        return response.body!!
    }

    override fun analyze(input: LinterInput): LinterOutput {
        val linterUrl = "$runnerUrl/analyze"
        val response = restTemplate.postForEntity(linterUrl, input, LinterOutput::class.java)
        return response.body!!
    }
}
