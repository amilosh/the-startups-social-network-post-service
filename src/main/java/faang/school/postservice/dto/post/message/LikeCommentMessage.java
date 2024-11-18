package faang.school.postservice.dto.post.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeCommentMessage {
    private Long postId;
    private Long commentId;
}
