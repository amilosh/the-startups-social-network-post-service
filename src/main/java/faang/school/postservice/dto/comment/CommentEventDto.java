package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class CommentEventDto {

    @NotNull
    private Long postCreatorId;

    @NotNull
    private Long commenterId;

    @NotNull
    private Long commentId;

    @NotNull
    private Long postId;
}
