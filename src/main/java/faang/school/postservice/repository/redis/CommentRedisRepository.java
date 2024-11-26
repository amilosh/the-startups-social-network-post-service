package faang.school.postservice.repository.redis;

import faang.school.postservice.model.event.CommentEvent;
import faang.school.postservice.model.redis.CommentRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CommentRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostRedisRepository postRedisRepository;

    @Value("${spring.data.redis.limit-size.comment:200}")
    private int limitSize;

    public void addCommentPost(CommentEvent comment) {
        postRedisRepository.getPost(comment.postId()).ifPresent(post -> {
            removeLastComment(post);
            CommentRedis commentRedis = CommentRedis.builder()
                    .likeKey(Constant.LIKE_COMMENT_KEY + comment.commentId())
                    .content(comment.content())
                    .commentAuthorId(comment.commentAuthorId())
                    .build();
            long timeStamp = comment.commentedAt().atZone(ZoneOffset.UTC).toInstant().getEpochSecond();
            redisTemplate.opsForZSet().add(post.getCommentKey(), commentRedis, timeStamp);
            redisTemplate.expire(post.getCommentKey(), getTimeToLivePost(post));
        });
    }

    public List<CommentEvent> getRecentComments(PostRedis post, long startPage, long endPage) {
        Set<Object> comments = redisTemplate.opsForZSet().range(post.getCommentKey(), startPage, endPage);
        return comments != null ? comments.stream()
                .map(comment -> (CommentEvent) comment)
                .toList() : null;
    }

    public long getCountCommentsByPost(PostRedis post) {
        Long count = redisTemplate.opsForZSet().size(post.getCommentKey());
        return count != null ? count : 0;
    }

    public void deleteComment(CommentEvent comment) {

    }

    private Duration getTimeToLivePost(PostRedis post) {
        log.info("Get time to live post: {}", post.getTimeToLive());
        return Duration.ofSeconds(post.getTimeToLive());
    }

    private void removeLastComment(PostRedis post){
        redisTemplate.opsForZSet().popMin(post.getCommentKey());
    }

}
