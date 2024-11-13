package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NotNull(message = "Like can't be empty")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {
    private Long id;

    @NotNull(message = "Like must have an author")
    private Long userId;
    private Long commentId;
    private Long postId;
}
