package faang.school.postservice.dto.cache.post;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@ToString
@RedisHash(value = "Post")
public class PostCacheDto implements Serializable {
    private Long postId;
    private String content;
    private Long authorId;
    private Long projectId;
    private int likesCount;
    private List<CommentDto> comments;
    private LocalDateTime createdAt;
}
