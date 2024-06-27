package ingsis.group12.snippetoperations.redis.producer

import ingsis.group12.snippetoperations.redis.input.ProducerRequest
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component
class SpyLinterProducer : LinterProducer {
    private var seen = emptyList<ProducerRequest>()

    override suspend fun publishEvent(request: ProducerRequest) {
        seen = seen + request
    }

    fun events() = seen

    fun reset() {
        seen = emptyList()
    }
}
