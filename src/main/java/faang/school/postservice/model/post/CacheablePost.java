package faang.school.postservice.model.post;


import faang.school.postservice.dto.comment.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash
public class CacheablePost {
    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private long countOfLikes;
    private long countOfComments;
    private List<CommentDto> comments;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
}
