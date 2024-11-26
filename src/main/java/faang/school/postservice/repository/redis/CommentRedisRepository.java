package faang.school.postservice.repository.redis;

import faang.school.postservice.model.event.CommentEvent;
import faang.school.postservice.model.redis.CommentRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
                    .key(Constant.COMMENT_KEY + comment.commentId())
                    .likeKey(Constant.LIKE_COMMENT_KEY + comment.commentId())
                    .content(comment.content())
                    .commentAuthorId(comment.commentAuthorId())
                    .build();
            long timeStamp = comment.commentedAt().atZone(ZoneOffset.UTC).toInstant().getEpochSecond();
            redisTemplate.opsForZSet().add(post.commentKey(), commentRedis, timeStamp);
            redisTemplate.expire(post.commentKey(), getTimeToLivePost(post));
            log.info("save comment post {}", commentRedis);
        });
    }

    public List<CommentEvent> getRecentComments(PostRedis post, long startPage, long endPage) {
        Set<Object> comments = redisTemplate.opsForZSet().range(post.commentKey(), startPage, endPage);
        return comments != null ? comments.stream()
                .map(comment -> (CommentEvent) comment)
                .toList() : Collections.emptyList();
    }

    public long getCountCommentsByPost(PostRedis post) {
        Long count = redisTemplate.opsForZSet().size(post.commentKey());
        return count != null ? count : 0;
    }

    public void deleteComment(CommentEvent comment) {
        postRedisRepository.getPost(comment.postId()).ifPresent(post ->
                redisTemplate.opsForZSet().remove(post.commentKey(), Constant.COMMENT_KEY + comment.commentId()));
    }

    private Duration getTimeToLivePost(PostRedis post) {
        Long timePost = redisTemplate.getExpire(post.key(), TimeUnit.SECONDS);
        return Duration.ofSeconds(timePost != null ? timePost : 0);
    }

    private void removeLastComment(PostRedis post){
        if(getCountCommentsByPost(post) > limitSize){
            ZSetOperations.TypedTuple<Object> lastPost = redisTemplate.opsForZSet().popMin(post.commentKey());
            log.info("Remove last comment for post: {}", lastPost.getValue());
        }
    }

}
