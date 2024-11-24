package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private Long authorId;
    private List<Long> likeIds;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
