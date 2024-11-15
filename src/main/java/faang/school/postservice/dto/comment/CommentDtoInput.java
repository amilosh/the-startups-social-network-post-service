package faang.school.postservice.dto.comment;

import lombok.Data;

import java.util.List;

@Data
public class CommentDtoInput {
    private Long id;
    private String content;
    private Long authorId;
    private List<Long> likeIds;
    private Long postId;
}