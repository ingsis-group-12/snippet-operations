package ingsis.group12.snippetoperations

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class SnippetOperationsApplication {
    @Bean
    fun docs(): OpenAPI {
        return OpenAPI()
            .info(
                Info().title("Operations Api").version("v1"),
            ).servers(emptyList())
    }
}

fun main(args: Array<String>) {
    runApplication<SnippetOperationsApplication>(*args)
}
