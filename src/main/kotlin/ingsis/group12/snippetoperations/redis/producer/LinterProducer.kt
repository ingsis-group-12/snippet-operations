package ingsis.group12.snippetoperations.redis.producer

import ingsis.group12.snippetoperations.redis.input.ProducerRequest
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisLinterProducer
    @Autowired
    constructor(
        @Value("\${stream.key}") streamKey: String,
        redis: RedisTemplate<String, String>,
    ) : LinterProducer, RedisStreamProducer(streamKey, redis) {
        override suspend fun publishEvent(request: ProducerRequest) {
            println(
                "Publishing on stream: $streamKey " +
                    "with snippet id : ${request.snippetId}",
            )
            emit(request)
        }
    }

interface LinterProducer {
    suspend fun publishEvent(request: ProducerRequest)
}
