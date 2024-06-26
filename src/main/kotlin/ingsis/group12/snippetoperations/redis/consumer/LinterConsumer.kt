package ingsis.group12.snippetoperations.redis.consumer

import ingsis.group12.snippetoperations.asset.input.SnippetUpdateInput
import ingsis.group12.snippetoperations.asset.service.SnippetService
import ingsis.group12.snippetoperations.redis.input.ProducerRequest
import org.austral.ingsis.redis.RedisStreamConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class LinterConsumer
    @Autowired
    constructor(
        redis: RedisTemplate<String, String>,
        @Value("\${stream.key}") streamKey: String,
        @Value("\${groups.product}") groupId: String,
        private val snippetService: SnippetService,
    ) : RedisStreamConsumer<ProducerRequest>(streamKey, groupId, redis) {
        override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, ProducerRequest>> {
            return StreamReceiver.StreamReceiverOptions.builder()
                .pollTimeout(Duration.ofMillis(10000)) // Set poll rate
                .targetType(ProducerRequest::class.java) // Set type to de-serialize record
                .build()
        }

        override fun onMessage(record: ObjectRecord<String, ProducerRequest>) {
            println("Received record with snippet id ${record.value.snippetId}")
            val snippetId = record.value.snippetId
            val userId = record.value.userId
            val snippet = snippetService.getAssetById(snippetId)
            snippetService.updateAsset(snippetId, SnippetUpdateInput(snippet.name, snippet.content), userId)
            println("Finished record with snippet id ${record.value.snippetId}")
        }
    }
