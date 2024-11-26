package faang.school.postservice.service;

import faang.school.postservice.dto.post.message.PostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${post.feed.max-size}")
    private int maxFeedSize;

    public void addPostToFeed(PostEvent postEvent) {

        double score = postEvent.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        postEvent.getSubscribers().forEach(subscriber -> {
            String feedKey = "feed:" + subscriber;
            redisTemplate.opsForZSet().add(feedKey, postEvent.getPostId(), score);

            Long size = redisTemplate.opsForZSet().size(feedKey);
            if (size != null && size > maxFeedSize) {
                redisTemplate.opsForZSet().removeRange(feedKey, 0, size - maxFeedSize - 1);
            }
        });
    }
}
