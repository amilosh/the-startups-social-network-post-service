package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LikeDto {
    private Long id;

    @NotNull(message = "User id is required")
    private Long userId;

    private Long commentId;
    private Long postId;
    private LocalDateTime createdAt;
}
