package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final LikeMapper likeMapper;

    public LikePostDto likePost(long postId, long userId) {
        validateUserExisted(userId);

        if (isPostLikedByUser(postId, userId)) {
            log.error("User {} cannot like this post {}, already liked", userId, postId);
            throw new DataValidationException("You already liked this post");
        }

        if(postService.isPostNotExist(postId)){
            log.error("User {} cannot like this post {}, this post does not exist", userId, postId);
            throw new DataValidationException("This post does not exist");
        }

//        Like like = likeMapper.toLike(likeDto);
        Like like = Like.builder()
                .userId(userId)
                .post(postService.getPostById(postId))
                .build();
        like.setPost(postService.getPostById(postId));
        Like savedLike = likeRepository.save(like);

        log.info("User {} successfully liked post {}. Like saved with ID {}", userId, postId, savedLike.getId());
        return likeMapper.toLikePostDto(savedLike);
    }

    public Like getLikeByPostIdAndUserId(long postId, long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Like is not found"));
    }

    private void validateUserExisted(long userId) {
//        try {
            userServiceClient.getUser(userId);
//        } catch (FeignException e) {
//            log.error("User is not found with id {}", userId);
//            throw new EntityNotFoundException("User is not found", e);
//        }
    }

    private boolean isPostLikedByUser(long postId, long userId) {
        boolean isLiked = likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
        log.debug("Post {} liked by user {}: {}", postId, userId, isLiked);
        return isLiked;
    }

}

