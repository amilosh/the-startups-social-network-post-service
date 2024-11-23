package faang.school.postservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeDto {
    @NotNull(message = "Id cannot be null")
    private Long id;
    @NotNull(message = "User id cannot be null")
    private Long userId;
    private Long commentId;
    private Long postId;
    private LocalDateTime createdAt;
}
