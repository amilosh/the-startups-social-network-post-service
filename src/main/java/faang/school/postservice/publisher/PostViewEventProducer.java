package faang.school.postservice.publisher;

import faang.school.postservice.model.event.newsfeed.PostViewEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostViewEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic postViewNFTopic;

    public void produce(PostViewEvent postViewEvent) {
        kafkaTemplate.send(postViewNFTopic.name(), postViewEvent);
    }
}
