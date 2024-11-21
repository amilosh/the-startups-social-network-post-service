package faang.school.postservice.model.entity.redis;

import faang.school.postservice.model.dto.comment.CommentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "posts")
public class PostRedis implements Serializable {
    private long id;
    private String title;
    private String content;
    private Long authorId;
    private Long likes;
    private List<CommentResponseDto> comments;
    private Long views;
}
