package faang.school.postservice.dto.comment;

import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDto {

    private long id;
    private String content;
    private long authorId;
    private List<Like> likes;
    private Post post;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
