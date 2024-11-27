package faang.school.postservice.model.redis;

import faang.school.postservice.model.dto.comment.CommentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRedis {
    private long id;
    private String title;
    private String content;
    private Long authorId;
    private Long likes;
    private List<CommentResponseDto> comments;
    private Long views;
}
