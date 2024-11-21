package faang.school.postservice.publisher;

import faang.school.postservice.model.event.newsfeed.LikeNewsFeedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeNewsFeedProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic likeNFTopic;

    public void produce(LikeNewsFeedEvent likeEvent) {
        kafkaTemplate.send(likeNFTopic.name(), likeEvent);
    }
}
