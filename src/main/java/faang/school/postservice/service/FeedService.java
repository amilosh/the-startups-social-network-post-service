package faang.school.postservice.service;

import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.event.kafka.PostPublishedEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedService {

    void addPost(PostPublishedEvent event);

    List<RedisPostDto> getNewsFeed(Long userId, int page, int pageSize);

}
