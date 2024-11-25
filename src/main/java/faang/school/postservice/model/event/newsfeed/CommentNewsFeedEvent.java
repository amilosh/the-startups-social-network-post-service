package faang.school.postservice.model.event.newsfeed;

import lombok.Builder;

@Builder
public record CommentNewsFeedEvent(
        long id,
        long postId,
        String content,
        long authorId
) {
}
