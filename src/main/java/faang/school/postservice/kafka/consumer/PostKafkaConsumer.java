package faang.school.postservice.kafka.consumer;

import faang.school.postservice.model.event.kafka.PostCreatedEvent;
import faang.school.postservice.service.FeedService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PostKafkaConsumer extends AbstractKafkaConsumer<PostCreatedEvent> {

    @Value("${kafka.topics.post}")
    private String topic;

    @Value("${kafka.groups.post}")
    private String groupId;

    private final FeedService feedService;

    public PostKafkaConsumer(FeedService feedService) {
        super(PostCreatedEvent.class);
        this.feedService = feedService;
    }

    @Override
    protected void processEvent(PostCreatedEvent event) {
        feedService.processPostCreatedEvent(event);
    }

    @Override
    protected String getTopic() {
        return topic;
    }

    @Override
    protected String getGroupId() {
        return groupId;
    }
}
