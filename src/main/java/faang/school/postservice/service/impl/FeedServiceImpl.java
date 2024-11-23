package faang.school.postservice.service.impl;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.comment.CommentPublishedEvent;
import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.mapper.post.CacheablePostMapper;
import faang.school.postservice.model.Feed;
import faang.school.postservice.model.post.CacheablePost;
import faang.school.postservice.repository.FeedCacheRepository;
import faang.school.postservice.repository.post.PostCacheRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.service.FeedService;
import jakarta.persistence.EntityNotFoundException;
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
    private final FeedCacheRepository feedCacheRepository;
    private final PostCacheRepository postCacheRepository;
    private final PostRepository postRepository;
    private final CacheablePostMapper cacheablePostMapper;

    @Value("${feed.cache.count}")
    private int cacheCount;

    @Override
    @Transactional
    public void distributePostsToUsersFeeds(PostPublishedEvent event) {
        List<Long> subsIds = event.getSubscribersIds();

        subsIds.forEach(id -> updateFeed(id, event.getPostId()));
    }

    @Override
    public void addNewComment(CommentPublishedEvent commentEvent) {
        CacheablePost post = postCacheRepository.findById(commentEvent.getPostId())
                .orElse(cacheablePostMapper.toCacheablePost(
                        postRepository.getReferenceById(commentEvent.getPostId()))
                );
        if (post == null) {
            throw new EntityNotFoundException("post with id = " + commentEvent.getPostId() + " not found");
        }


    }

    @Retryable(maxAttempts = 5, retryFor = {OptimisticEntityLockException.class})
    private void updateFeed(long userId, long postId) {
        Feed feed = feedCacheRepository.findById(userId).orElse(new Feed(userId, new LinkedHashSet<>()));

        redisTemplate.watch(userId);
        try {
            addNewPostToFeed(feed, postId);
            feedCacheRepository.save(feed);
        } catch (Exception e) {
            redisTemplate.discard();
            throw e;
        }

        if (redisTemplate.exec().isEmpty()) {
            throw new OptimisticEntityLockException(feed, "(((");
        }
    }

    private void addNewPostToFeed(Feed feed, long postId) {
        var postIds = feed.getPostIds();
        postIds.add(postId);
        if (postIds.size() > cacheCount) {
            postIds.remove(postIds.iterator().next());
        }

        feed.setPostIds(postIds);
    }

    private void addCommentToPost(CommentPublishedEvent event) {


    }
}
