package faang.school.postservice.service;

import faang.school.postservice.model.dto.FeedDto;

public interface FeedService {
    FeedDto getFeed (Long feedId, Long userId, Integer startPostId);
}
