package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LikeCommentDto(
        @NotNull(groups = {After.class})
        Long id,
        @NotNull(groups = {Before.class})
        Long userId,
        @NotNull(groups = {Before.class})
        Long postId,
        @NotNull(groups = {Before.class})
        Long commentId,
        Long numberOfLikes
) {
    public interface After {}
    public interface Before {}
}
