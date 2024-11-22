package faang.school.postservice.service;

import faang.school.postservice.model.event.kafka.PostCreatedEvent;

public interface FeedService {
    void processPostCreatedEvent(PostCreatedEvent event);
}
