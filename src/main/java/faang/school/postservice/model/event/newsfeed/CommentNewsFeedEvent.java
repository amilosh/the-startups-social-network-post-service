package faang.school.postservice.model.event.newsfeed;

import faang.school.postservice.model.dto.user.UserDto;
import lombok.Builder;

@Builder
public record CommentNewsFeedEvent(
        long id,
        long postId,
        String content,
        long authorId,
        UserDto user
) {
}
