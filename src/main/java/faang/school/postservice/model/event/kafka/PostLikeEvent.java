package faang.school.postservice.model.event.kafka;

import lombok.Builder;

@Builder
public record PostLikeEvent(
        long postId,
        long authorId
) {}
