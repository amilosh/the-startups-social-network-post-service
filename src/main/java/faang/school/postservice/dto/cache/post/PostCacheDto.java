package faang.school.postservice.dto.cache.post;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PostCacheDto implements Serializable {

    private Long postId;
    private String content;
    private Long authorId;
    private Long projectId;
    private int likesCount;
    private List<CommentDto> comments;
    private LocalDateTime createdAt;
}
