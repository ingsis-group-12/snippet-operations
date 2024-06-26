package ingsis.group12.snippetoperations.redis.producer

import ingsis.group12.snippetoperations.redis.input.ProducerRequest
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestComponent
import org.springframework.data.redis.core.RedisTemplate

@TestComponent
class LinterProducer
    @Autowired
    constructor(
        @Value("\${stream.key}") streamKey: String,
        redis: RedisTemplate<String, String>,
    ) : RedisStreamProducer(streamKey, redis) {
        suspend fun publishEvent(request: ProducerRequest) {
            println(
                "Publishing on stream: $streamKey " +
                    "with snippet id : ${request.snippetId}",
            )
            emit(request)
        }


    }
