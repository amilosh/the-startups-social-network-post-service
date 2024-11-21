package faang.school.postservice.model.event.newsfeed;

import lombok.Builder;

@Builder
public record LikeNewsFeedEvent(
        long postId,
        long authorID
) {
}
