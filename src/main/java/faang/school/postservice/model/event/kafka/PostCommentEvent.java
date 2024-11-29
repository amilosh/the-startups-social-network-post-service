package faang.school.postservice.model.event.kafka;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostCommentEvent(
        long id,
        long postId,
        long authorId,
        String content,
        LocalDateTime createdAt) {
}
