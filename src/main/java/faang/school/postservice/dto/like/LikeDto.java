package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeDto {
    private Long id;
    @Min(1) @NotNull(message = "User id is required")
    private Long userId;
    private Long postId;
    private Long commentId;
    private LocalDateTime createdAt;
}