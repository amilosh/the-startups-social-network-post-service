package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {

    @Min(value = 1, message = "ID must be a positive number")
    private Long id;

    @Min(value = 1, message = "ID must be a positive number")
    private Long userId;

    private Long postId;

    private LocalDateTime likeDate;
}
