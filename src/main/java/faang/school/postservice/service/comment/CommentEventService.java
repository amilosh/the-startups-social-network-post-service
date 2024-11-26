package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.comment.Comment;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.publisher.comment.RedisCommentEventPublisher;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class CommentEventService {
    private final PostService postService;
    private final RedisCommentEventPublisher commentEventPublisher;
    private final CommentMapper commentMapper;

    public void handleCommentEvent(Long postId, Comment savedComment) {
        Post post = postService.findPostById(postId);

        if (!post.getAuthorId().equals(savedComment.getAuthorId())) {
            CommentEvent event = commentMapper.toCommentEvent(postId, savedComment);
            commentEventPublisher.publishCommentEvent(event);
        }
    }
}