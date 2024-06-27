package ingsis.group12.snippetoperations.runner.service

import ingsis.group12.snippetoperations.runner.input.ExecutorInput
import ingsis.group12.snippetoperations.runner.input.FormatterInput
import ingsis.group12.snippetoperations.runner.input.LinterInput
import ingsis.group12.snippetoperations.runner.output.ExecutorOutput
import ingsis.group12.snippetoperations.runner.output.FormatterOutput
import ingsis.group12.snippetoperations.runner.output.LinterOutput
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(SnippetRunnerService::class.java)

    override fun execute(input: ExecutorInput): ExecutorOutput {
        val executeUrl = "$runnerUrl/interpret"
        logger.info("Executing code snippet")
        val response = restTemplate.postForEntity(executeUrl, input, ExecutorOutput::class.java)
        logger.info("Code snippet executed")
        return response.body!!
    }

    override fun format(input: FormatterInput): FormatterOutput {
        val formatUrl = "$runnerUrl/format"
        logger.info("Formatting code snippet")
        val response = restTemplate.postForEntity(formatUrl, input, FormatterOutput::class.java)
        logger.info("Code snippet formatted")
        return response.body!!
    }

    override fun analyze(input: LinterInput): LinterOutput {
        val linterUrl = "$runnerUrl/analyze"
        logger.info("Analyzing code snippet")
        val response = restTemplate.postForEntity(linterUrl, input, LinterOutput::class.java)
        logger.info("Code snippet analyzed")
        return response.body!!
    }
}
