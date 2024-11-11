package faang.school.postservice.dto.comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class CommentDto {
    private long id;
    private String content;
    private long authorId;
    private List<Long> likeIds;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
