package faang.school.postservice.dto.cache.post;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Builder
@ToString
@RedisHash(value = "Post")
public class PostCacheDto implements Serializable {
    @Id
    private Long postId;
    private String content;
    private Long authorId;
    private Long likesCount;
    private Long commentsCount;
}
