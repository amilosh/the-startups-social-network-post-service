package faang.school.postservice.dto.comment;

import lombok.Data;

@Data
public class CommentDtoInput {
    private Long id;
    private String content;
    private Long authorId;
    private Long postId;
}