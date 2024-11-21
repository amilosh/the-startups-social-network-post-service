package faang.school.postservice.model.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LikeEvent(
        long id,
        long postId,
        long likeAuthorId,
        long postAuthorId,
        LocalDateTime likedTime
) {
}
