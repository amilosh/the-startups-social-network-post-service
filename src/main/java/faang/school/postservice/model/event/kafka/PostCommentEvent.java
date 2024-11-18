package faang.school.postservice.model.event.kafka;

import lombok.Builder;

@Builder
public record PostCommentEvent(
        long id,
        long postId,
        long authorId) {
}
