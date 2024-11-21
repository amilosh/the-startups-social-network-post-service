package faang.school.postservice.service.impl;

import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.model.Feed;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final RedisTemplate<Long, LinkedHashSet<Long>> redisTemplate;

    @Value("${feed.cache.count}")
    private int cacheCount;

    @Override
    @Transactional
    public void distributePostsToUsersFeeds(PostPublishedEvent event) {
        List<Long> subsIds = event.getSubscribersIds();


        subsIds.forEach(id -> updateFeed(id, event.getPostId()));
    }

    @Retryable(maxAttempts = 5, retryFor = {Exception.class})
    private void updateFeed(long userId, long postId) {
        LinkedHashSet<Long> postIds = redisTemplate.opsForValue().get(userId);

        redisTemplate.multi();
        try {
            editPostIds(postIds, userId, postId);
        } catch (Exception e) {
            redisTemplate.discard();
            throw e;
        }

        if (redisTemplate.exec().isEmpty()) {
            throw new OptimisticEntityLockException(new Feed(userId, postIds), "(((");
        }
    }

    private void editPostIds(LinkedHashSet<Long> postIds, long userId, long postId) {
        if (postIds == null) {
            postIds = new LinkedHashSet<>();
        }
        postIds.add(postId);
        if (postIds.size() > cacheCount) {
            postIds.remove(postIds.iterator().next());
        }

        redisTemplate.opsForValue().set(userId, postIds);
    }
}
