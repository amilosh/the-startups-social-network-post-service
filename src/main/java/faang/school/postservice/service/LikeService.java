package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
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

    public LikeDto likePost(long postId, LikeDto likeDto) {
        log.info("Creating like with likeDto {} and postId {}", likeDto, postId);
        long userId = likeDto.userId();
        userServiceClient.getUser(userId);

        if (isPostLikedByUser(postId, userId)) {
            log.error("User {} cannot like this post {}, already liked", userId, postId);
            throw new DataValidationException("You already liked this post");
        }

        if(postService.isPostNotExist(postId)){
            log.error("User {} cannot like this post {}, this post does not exist", userId, postId);
            throw new DataValidationException("This post does not exist");
        }

        Like like = likeMapper.toLike(likeDto);
        like.setPost(postService.getPostById(postId));
        Like savedLike = likeRepository.save(like);

        log.info("User {} successfully liked post {} with {} ", userId, postId, savedLike);
        return likeMapper.toLikePostDto(savedLike);
    }


    public LikeDto likeComment(long commentId, LikeDto likeDto){
        log.info("Creating like with likeDto {} and commentId {}", likeDto, commentId);
        long userId = likeDto.userId();
        userServiceClient.getUser(userId);

        if (isCommentLikedByUser(commentId, userId)) {
            log.error("User {} cannot like this comment {}, already liked", userId, commentId);
            throw new DataValidationException("You already liked this post");
        }

        if(commentService.isCommentNotExist(commentId)){
            log.error("User {} cannot like this post {}, this post does not exist", userId, commentId);
            throw new DataValidationException("This comment does not exist");
        }

        Like like = likeMapper.toLike(likeDto);
        like.setComment(commentService.getCommentById(commentId));
        Like savedLike = likeRepository.save(like);

        log.info("User {} successfully liked post {} with {} ", userId, commentId, savedLike);
        return likeMapper.toLikePostDto(savedLike);
    }


    @Transactional
    public void deleteLikeFromPost(long postId, LikeDto likeDto){
        long userId = likeDto.userId();
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("Successfully deleted like for postId: {} by userId: {}", postId, userId);
    }

    @Transactional
    public void deleteLikeFromComment(long commentId, LikeDto likeDto){
        long userId = likeDto.userId();
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("Successfully deleted like for comment: {} by userId: {}", commentId, userId);
    }

    private void validateAlreadyLiked(long postId, long commentId, long userId){
        if (isPostLikedByUser(postId, userId) || isCommentLikedByUser(commentId, userId)) {
            log.error("User {} cannot like this, already liked", userId);
            throw new DataValidationException("You already liked");
        }
    }

    private boolean isPostLikedByUser(long postId, long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }


    private boolean isCommentLikedByUser(long commentId, long userId){
        return likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent();
    }



    public Like getLikeByPostIdAndUserId(long postId, long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Like is not found"));
    }
}

