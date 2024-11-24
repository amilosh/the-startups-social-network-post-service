package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    private Long id;
    private Long userId;
    private Long commentId;
    private Long postId;
    private String createdAt;
}
