package faang.school.postservice.service.like;

import faang.school.postservice.annotations.PublishPostLikeEvent;
import faang.school.postservice.annotations.SendUserActionToCounter;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.RecordAlreadyExistsException;
import faang.school.postservice.exception.like.LikeNotFoundException;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.comment.Comment;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static faang.school.postservice.service.counter.enumeration.ChangeType.DECREMENT;
import static faang.school.postservice.service.counter.enumeration.ChangeType.INCREMENT;
import static faang.school.postservice.service.counter.enumeration.UserAction.COMMENT_LIKE;
import static faang.school.postservice.service.counter.enumeration.UserAction.POST_LIKE;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserValidator userValidator;
    private final PostService postService;
    private final CommentService commentService;
    private final UserContext userContext;

    @PublishPostLikeEvent
    @SendUserActionToCounter(userAction = POST_LIKE, changeType = INCREMENT, type = Like.class)
    @Transactional
    public Like createPostLike(long postId) {
        Post post = postService.findPostById(postId);
        long userId = userContext.getUserId();
        userValidator.validateUserExists(userId);

        if (hasUserLikedPost(postId, userId)) {
            throw new RecordAlreadyExistsException(
                    String.format("User %d already liked post with id %d", userId, postId)
            );
        }

        Like like = Like.builder()
                .post(post)
                .userId(userId)
                .build();

        return likeRepository.save(like);
    }

    @SendUserActionToCounter(userAction = POST_LIKE, changeType = DECREMENT, type = Post.class)
    @Transactional
    public Post deletePostLike(long postId) {
        Post post = postService.findPostById(postId);
        long userId = userContext.getUserId();
        userValidator.validateUserExists(userId);

        if (!hasUserLikedPost(postId, userId)) {
            throw new LikeNotFoundException(
                    String.format("User ID %d that liked post id %d not found.", userId, postId)
            );
        }

        likeRepository.deleteByPostIdAndUserId(postId, userId);
        return post;
    }

    @SendUserActionToCounter(userAction = COMMENT_LIKE, changeType = INCREMENT, type = Like.class)
    @Transactional
    public Like createCommentLike(long commentId) {
        Comment comment = commentService.getById(commentId);
        long userId = userContext.getUserId();
        userValidator.validateUserExists(userId);

        if (hasUserLikedComment(commentId, userId)) {
            throw new RecordAlreadyExistsException(
                    String.format("User %d already liked comment with id %d", userId, commentId)
            );
        }

        Like like = Like.builder()
                .comment(comment)
                .userId(userId)
                .build();

        return likeRepository.save(like);
    }

    @SendUserActionToCounter(userAction = COMMENT_LIKE, changeType = DECREMENT, type = Comment.class)
    @Transactional
    public Comment deleteCommentLike(long commentId) {
        Comment comment = commentService.getById(commentId);
        long userId = userContext.getUserId();
        userValidator.validateUserExists(userId);

        if (!hasUserLikedComment(commentId, userId)) {
            throw new LikeNotFoundException(
                    String.format("User ID %d that liked comment id %d not found.", userId, commentId)
            );
        }

        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        return comment;
    }

    private boolean hasUserLikedPost(long postId, long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

    private boolean hasUserLikedComment(long commentId, long userId) {
        return likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent();
    }
}
