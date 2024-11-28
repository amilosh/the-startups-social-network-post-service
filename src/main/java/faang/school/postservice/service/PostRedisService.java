package faang.school.postservice.service;

import faang.school.postservice.model.event.kafka.PostLikeEvent;
import faang.school.postservice.model.event.kafka.PostObservationEvent;
import faang.school.postservice.model.redis.PostRedis;

public interface PostRedisService {
    void saveLikeOnPost(PostLikeEvent event);

    void incrementViews(PostObservationEvent event);
}
