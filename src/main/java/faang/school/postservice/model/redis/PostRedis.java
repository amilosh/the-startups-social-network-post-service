package faang.school.postservice.model.redis;

import faang.school.postservice.model.dto.comment.CommentResponseDto;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@RedisHash("Post")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRedis implements Serializable {
    @Id
    private long id;
    private String title;
    private String content;
    private Long authorId;
    private Long likes;
    private List<CommentResponseDto> comments;
    private Long views;
}
