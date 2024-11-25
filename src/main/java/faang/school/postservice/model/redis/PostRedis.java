package faang.school.postservice.model.redis;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@RedisHash(value = "Posts")
public class PostRedis implements Serializable {

    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private Long countLikes;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;

    @TimeToLive
    private long timeToLive;

    public PostRedis(Long id, String content, Long authorId, Long projectId, Long countLikes,
                     LocalDateTime createdAt, LocalDateTime publishedAt, LocalDateTime updatedAt,
                     @Value("${cache.post.live-time}") long timeToLive) {
        this.id = id;
        this.content = content;
        this.authorId = authorId;
        this.projectId = projectId;
        this.countLikes = countLikes;
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
        this.updatedAt = updatedAt;
        this.timeToLive = timeToLive;
    }

}