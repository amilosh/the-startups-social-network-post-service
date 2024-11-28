package faang.school.postservice.consumer;

import faang.school.postservice.model.entity.redis.CommentCache;
import faang.school.postservice.model.entity.redis.RedisUser;
import faang.school.postservice.model.event.newsfeed.CommentNewsFeedEvent;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;

@Component
@RequiredArgsConstructor
public class CommentConsumer {
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;

    @Value("${news-feed.comment-capacity}")
    private int commentCapacity;

    @KafkaListener(topics = "${spring.data.kafka.channels.comment-nf-topic}", groupId = "${spring.data.kafka.group}")
    public void consume(CommentNewsFeedEvent event, Acknowledgment ack) {
        redisPostRepository.findById(event.postId()).ifPresent(post -> {
            var commentCache = CommentCache.builder()
                    .id(event.id())
                    .content(event.content())
                    .authorId(event.authorId())
                    .build();
            var commentator = RedisUser.builder()
                    .id(event.authorId())
                    .email(event.user().email())
                    .username(event.user().username())
                    .build();
            var comments = post.getComments();
            if (comments == null) {
                comments = new LinkedHashSet<>();
            }
            if (comments.size() >= commentCapacity) {
                if (comments.iterator().hasNext()) {
                    comments.remove(comments.iterator().next());
                }
            }
            comments.add(commentCache);
            post.setComments(comments);
            redisPostRepository.save(post);
            redisUserRepository.save(commentator);
        });
        ack.acknowledge();
    }
}
