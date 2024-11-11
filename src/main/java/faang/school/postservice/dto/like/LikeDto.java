package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NotNull
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {
    private Long id;
    private Long commentId;
    private Long postId;
}
