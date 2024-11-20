package faang.school.postservice.model.dto.like;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class LikeDto {
    @Positive
    private Long Id;

    @Positive
    private Long userId;

    @Positive
    private Long commentId;

    @Positive
    private Long postId;
}
