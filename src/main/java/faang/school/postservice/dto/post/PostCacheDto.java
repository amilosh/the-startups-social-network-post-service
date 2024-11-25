package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "Post")
public class PostCacheDto implements Serializable {

    @Id
    private Long postId;
    private String content;
    private Long authorId;
    private Long likeAuthorId;
    private Long likesCount;
    private Long commentsCount;
}