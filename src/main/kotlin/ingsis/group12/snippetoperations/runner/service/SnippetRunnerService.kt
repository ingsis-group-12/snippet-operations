package ingsis.group12.snippetoperations.runner.service

import ingsis.group12.snippetoperations.relic.CorrelationIdFilter
import ingsis.group12.snippetoperations.runner.input.ExecutorInput
import ingsis.group12.snippetoperations.runner.input.FormatterInput
import ingsis.group12.snippetoperations.runner.input.LinterInput
import ingsis.group12.snippetoperations.runner.output.ExecutorOutput
import ingsis.group12.snippetoperations.runner.output.FormatterOutput
import ingsis.group12.snippetoperations.runner.output.LinterOutput
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
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
        val correlationIdKey = MDC.get(CorrelationIdFilter.CORRELATION_ID_KEY)
        val executeUrl = "$runnerUrl/interpret"

        // Crea los headers
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Correlation-Id", correlationIdKey)

        // Crea HttpEntity con el cuerpo y los headers
        val requestEntity = HttpEntity(input, headers)

        logger.info("Executing code snippet")
        val response = restTemplate.postForEntity(executeUrl, requestEntity, ExecutorOutput::class.java)
        logger.info("Code snippet executed")
        return response.body!!
    }

    override fun format(input: FormatterInput): FormatterOutput {
        val correlationIdKey = MDC.get(CorrelationIdFilter.CORRELATION_ID_KEY)
        val formatUrl = "$runnerUrl/format"

        // Crea los headers
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Correlation-Id", correlationIdKey)

        // Crea HttpEntity con el cuerpo y los headers
        val requestEntity = HttpEntity(input, headers)

        logger.info("Formatting code snippet")
        val response = restTemplate.postForEntity(formatUrl, requestEntity, FormatterOutput::class.java)
        logger.info("Code snippet formatted")
        return response.body!!
    }

    override fun analyze(input: LinterInput): LinterOutput {
        val correlationIdKey = MDC.get(CorrelationIdFilter.CORRELATION_ID_KEY)
        val linterUrl = "$runnerUrl/analyze"

        // Crea los headers
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.set("X-Correlation-Id", correlationIdKey)

        // Crea HttpEntity con el cuerpo y los headers
        val requestEntity = HttpEntity(input, headers)

        logger.info("Analyzing code snippet")
        val response = restTemplate.postForEntity(linterUrl, requestEntity, LinterOutput::class.java)
        logger.info("Code snippet analyzed")
        return response.body!!
    }
}
