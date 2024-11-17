package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeMapper likeMapper;

    @Transactional
    public LikePostDto createLikePost(long postId, long userId) {
        log.info("Creating like with userId {} and postId {}", userId, postId);

        validateUserExists(userId);
        checkPostExists(postId);
        validatePostLiked(postId, userId);

        Like like = Like.builder()
                .userId(userId)
                .post(postService.getPostById(postId))
                .build();

        Like savedLike = likeRepository.save(like);

        log.info("UserId {} successfully liked postId {} with {} ", userId, postId, savedLike);
        return likeMapper.toLikePostDto(savedLike);
    }

    private void validateUserExists(long userId){
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e){
            throw new EntityNotFoundException("User is not found by " + userId);
        }
    }

    private void checkPostExists(long postId) {
        if (postService.isPostNotExist(postId)) {
            log.error("Post id {} does not exist", postId);
            throw new EntityNotFoundException("This post does not exist");
        }
    }

    private void validatePostLiked(long postId, long userId) {
        if (isPostLikedByUser(postId, userId)) {
            log.error("User {} cannot like this post id {}, already liked", userId, postId);
            throw new DataValidationException("You already liked this post");
        }
    }


    @Transactional
    public LikeCommentDto createLikeComment(long commentId, long userId) {
        log.info("Creating like with user {} and commentId {}", userId, commentId);

        validateUserExists(userId);
        validateCommentExists(commentId);
        validateCommentLiked(commentId, userId);

        Like like = Like.builder()
                .userId(userId)
                .comment(commentService.getCommentById(commentId))
                .build();

        Like savedLike = likeRepository.save(like);

        log.info("User {} successfully liked comment {} with {} ", userId, commentId, savedLike);
        return likeMapper.toLikeCommentDto(savedLike);
    }

    private void validateCommentExists(long commentId) {
        if (commentService.isCommentNotExist(commentId)) {
            log.error("Comment id {} does not exist", commentId);
            throw new EntityNotFoundException("This comment does not exist");
        }
    }

    private void validateCommentLiked(long commentId, long userId) {
        if (isCommentLikedByUser(commentId, userId)) {
            log.error("User {} cannot like this comment id {}, already liked", userId, commentId);
            throw new DataValidationException("You already liked this comment");
        }
    }

    @Transactional
    public void deleteLikeFromPost(long postId, long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("Successfully deleted like for postId: {} by userId: {}", postId, userId);
    }

    @Transactional
    public void deleteLikeFromComment(long commentId, long userId) {
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("Successfully deleted like for comment: {} by userId: {}", commentId, userId);
    }

    private boolean isPostLikedByUser(long postId, long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

    private boolean isCommentLikedByUser(long commentId, long userId) {
        return likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent();
    }
}