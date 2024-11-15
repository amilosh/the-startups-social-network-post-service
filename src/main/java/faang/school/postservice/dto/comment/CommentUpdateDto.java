package faang.school.postservice.dto.comment;

import lombok.Data;

@Data
public class CommentUpdateDto {
    private Long commentId;
    private String content;
}