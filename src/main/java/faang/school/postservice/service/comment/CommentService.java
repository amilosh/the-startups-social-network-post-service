package faang.school.postservice.service.comment;

import faang.school.postservice.annotations.PublishCommentEvent;
import faang.school.postservice.annotations.PublishCommentNotificationEvent;
import faang.school.postservice.annotations.publisher.PublishEvent;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.serializable.CommentCacheDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.cache.CommentCacheRepository;
import faang.school.postservice.repository.cache.UserCacheRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static faang.school.postservice.enums.publisher.PublisherType.COMMENT_POST;


@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CommentValidator commentValidator;
    private final UserServiceClient userServiceClient;
    private final UserCacheRepository userCacheRepository;
    private final CommentCacheRepository commentCacheRepository;
    private final CommentMapper commentMapper;
    private final UserContext userContext;

    @PublishEvent(type = COMMENT_POST)
//    @PublishCommentEvent
//    @PublishCommentNotificationEvent
    @Transactional
    public Comment createComment(Long postId, Comment comment) {
        commentValidator.validateCreate(postId, comment);
        Post post = postService.findPostById(postId);
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);

        UserDto userDto = userServiceClient.getUser(userContext.getUserId());
        userCacheRepository.save(userDto);
        CommentCacheDto commentCacheDto = commentMapper.toCommentCacheDto(comment);
        commentCacheRepository.save(commentCacheDto);

        return savedComment;
    }

    @Transactional
    public Comment updateComment(Long commentId, Comment comment) {
        var foundComment = getById(commentId);
        commentValidator.validateCommentAuthorId(comment.getAuthorId(), foundComment);
        foundComment.setContent(comment.getContent());

        CommentCacheDto commentCacheDto = commentMapper.toCommentCacheDto(comment);
        commentCacheRepository.save(commentCacheDto);

        return commentRepository.save(foundComment);
    }

    public Collection<Comment> getAllCommentsByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId).stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void delete(Long commentId) {
        getById(commentId);
        commentRepository.deleteById(commentId);

        commentCacheRepository.deleteById(commentId);
    }

    public Comment getById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(
                        () -> new CommentNotFoundException("Comment not found")
                );
    }
}
