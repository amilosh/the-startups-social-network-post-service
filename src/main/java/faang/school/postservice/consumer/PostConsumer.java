package faang.school.postservice.consumer;

import faang.school.postservice.model.entity.redis.PostCache;
import faang.school.postservice.model.event.newsfeed.PostNewsFeedEvent;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostConsumer {
    private final PostRepository postRepository;
    private final RedisPostRepository redisPostRepository;

    @KafkaListener(topics = "${spring.data.kafka.channels.post-channel}", groupId = "${spring.data.kafka.group}")
    public void consume(PostNewsFeedEvent event, Acknowledgment ack) {
        postRepository.findById(event.postId())
                .ifPresent(post -> {
                    var postCache = PostCache.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .authorId(post.getAuthorId())
                            .views(0L)
                            .likes(0L)
                            .build();
                    redisPostRepository.save(postCache);
                });
        ack.acknowledge();
    }
}
