package faang.school.postservice.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {

    private Long id;
    @Positive
    private Long postId;
    @Positive
    private Long commentId;
    private long authorId;

}