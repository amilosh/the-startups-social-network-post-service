package faang.school.postservice.service.impl;

import faang.school.postservice.model.event.kafka.PostCreatedEvent;
import faang.school.postservice.service.FeedService;

public class FeedServiceImpl implements FeedService {
    @Override
    public void processPostCreatedEvent(PostCreatedEvent event) {

    }
}
