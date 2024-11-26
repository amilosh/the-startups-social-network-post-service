package faang.school.postservice.service;

import faang.school.postservice.model.event.kafka.PostLikeEvent;

public interface LikePostService {
    void saveLikeOnPost(PostLikeEvent event);
}
