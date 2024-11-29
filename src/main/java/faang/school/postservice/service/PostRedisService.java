package faang.school.postservice.service;

import faang.school.postservice.model.event.kafka.PostCommentEvent;
import faang.school.postservice.model.event.kafka.PostLikeEvent;
import faang.school.postservice.model.event.kafka.PostObservationEvent;

public interface PostRedisService {
    void saveLikeOnPost(PostLikeEvent event);

    void incrementViews(PostObservationEvent event);

    void addComment(PostCommentEvent event);
}
