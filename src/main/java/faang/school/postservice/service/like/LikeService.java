package faang.school.postservice.service.like;

import faang.school.postservice.annotations.PublishPostLikeEvent;
import faang.school.postservice.annotations.SendCommentLikeEventToKafka;
import faang.school.postservice.annotations.SendCommentUnlikeEventToKafka;
import faang.school.postservice.annotations.SendPostLikeEventToKafka;
import faang.school.postservice.annotations.SendPostUnlikeEventToKafka;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeAction;
import faang.school.postservice.exception.RecordAlreadyExistsException;
import faang.school.postservice.exception.like.LikeNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @SendPostLikeEventToKafka(action = LikeAction.ADD)
    @Transactional
    public Like createPostLike(long postId) {
        Post post = postService.findPostById(postId);
        long userId = userContext.getUserId();
//        userValidator.validateUserExists(userId);

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

    @SendPostUnlikeEventToKafka(action = LikeAction.REMOVE)
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

    @SendCommentLikeEventToKafka(action = LikeAction.ADD)
    @Transactional
    public Like createCommentLike(long commentId) {
        Comment comment = commentService.getById(commentId);
        long userId = userContext.getUserId();
//        userValidator.validateUserExists(userId);

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

    @SendCommentUnlikeEventToKafka(action = LikeAction.REMOVE)
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
