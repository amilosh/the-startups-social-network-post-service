package faang.school.postservice.consumer;

import faang.school.postservice.model.entity.redis.AuthorCache;
import faang.school.postservice.model.entity.redis.PostCache;
import faang.school.postservice.model.entity.redis.SubscribersCache;
import faang.school.postservice.model.event.newsfeed.PostNewsFeedEvent;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.AuthorCacheRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisSubscribersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PostConsumer {
    private final PostRepository postRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisSubscribersRepository redisUserRepository;
    private final AuthorCacheRepository authorCacheRepository;

    @KafkaListener(topics = "${spring.data.kafka.channels.post-channel}", groupId = "${spring.data.kafka.group}")
    public void consume(PostNewsFeedEvent event, Acknowledgment ack) {
        postRepository.findById(event.postId()).ifPresent(post -> {
            var postCache = PostCache.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .authorId(post.getAuthorId())
                    .views(0L)
                    .likes(0L)
                    .build();
            redisPostRepository.save(postCache);

            authorCacheRepository.findById(post.getAuthorId()).ifPresentOrElse(author -> {
                author.getPostIds().add(post.getId());
                authorCacheRepository.save(author);
            }, () -> {
                var authorCache = new AuthorCache(post.getAuthorId(), new LinkedHashSet<>(Set.of(post.getId())));
                authorCacheRepository.save(authorCache);
            });

            event.subscribers().forEach(userId -> redisUserRepository.findById(userId).ifPresentOrElse(userCache -> {
                userCache.getPostIds().add(post.getId());
                redisUserRepository.save(userCache);
            }, () -> {
                var newUserCache = SubscribersCache.builder()
                        .userId(userId)
                        .postIds(new LinkedHashSet<>(Set.of(post.getId())))
                        .build();
                redisUserRepository.save(newUserCache);
            }));
        });
        ack.acknowledge();
    }
}
