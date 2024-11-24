package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.publisher.EventPublisher;

public class KafkaPostViewProducer implements EventPublisher<PostViewEvent> {
    @Override
    public void publish(PostViewEvent event) {

    }
}
