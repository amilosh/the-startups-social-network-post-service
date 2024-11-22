package faang.school.postservice.dto.post;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@Builder
@RedisHash("Post")
public class PostCacheDto implements Serializable {
    private Long postId;
    private String content;
    private Long authorId;
    private long likesCount;
    private long commentsCount;
}
