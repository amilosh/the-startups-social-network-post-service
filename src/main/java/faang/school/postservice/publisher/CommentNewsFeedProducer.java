package faang.school.postservice.publisher;

import faang.school.postservice.model.event.newsfeed.CommentNewsFeedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentNewsFeedProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic commentNFTopic;

    public void produce(CommentNewsFeedEvent event) {
        kafkaTemplate.send(commentNFTopic.name(), event);
    }
}
