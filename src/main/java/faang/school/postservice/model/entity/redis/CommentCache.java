package faang.school.postservice.model.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentCache {
    private Long id;
    private String content;
    private long authorId;
    private long postId;
}
