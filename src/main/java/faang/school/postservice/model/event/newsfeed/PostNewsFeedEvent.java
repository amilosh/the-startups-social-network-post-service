package faang.school.postservice.model.event.newsfeed;

import lombok.Builder;

import java.util.List;

@Builder
public record PostNewsFeedEvent(
        long authorId,
        long postId,
        List<Long> subscribers
) {
}
