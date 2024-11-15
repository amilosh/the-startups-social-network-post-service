package faang.school.postservice.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class CommentDtoOutputUponUpdate {
    private Long id;
    private String content;
    private long authorId;
    private List<Long> likeIds;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}