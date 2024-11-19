package faang.school.postservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeDto {
    @NotNull
    private Long id;
    @NotNull
    private Long userId;
    private Long commentId;
    private Long postId;
    private LocalDateTime createdAt;
}
