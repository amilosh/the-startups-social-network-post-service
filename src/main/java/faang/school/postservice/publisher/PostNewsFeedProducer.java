package faang.school.postservice.publisher;

import faang.school.postservice.model.event.newsfeed.PostNewsFeedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostNewsFeedProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic postNFTopic;

    public void publish(PostNewsFeedEvent event) {
        kafkaTemplate.send(postNFTopic.name(), event);
    }
}
