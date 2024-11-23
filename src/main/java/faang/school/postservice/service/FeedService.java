package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.post.PostPublishedEvent;

public interface FeedService {
    void distributePostsToUsersFeeds(PostPublishedEvent event);

    void addNewComment(CommentEvent commentEvent);
}
