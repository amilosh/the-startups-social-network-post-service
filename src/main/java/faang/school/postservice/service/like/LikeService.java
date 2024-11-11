package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostServiceImpl;
import faang.school.postservice.validator.comment.CommentValidator;
import faang.school.postservice.validator.like.LikeValidator;
import faang.school.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final PostServiceImpl postService;
    private final CommentService commentService;
    private final LikeValidator likeValidator;
    private final CommentValidator commentValidator;
    private final PostValidator postValidator;
    private final LikeMapper likeMapper;

    public void addLikeToPost(long userId, LikeDto dto) {
        validateUserExistence(userId);
        Post postOfLike = postValidator.validatePostExistence(dto.getPostId());
        likeValidator.validateLikeExistence(dto.getId());
        likeValidator.validateLikeWasNotPutToComment(dto);
        likeValidator.validateUserAddOnlyOneLikeToPost(dto.getPostId(), userId);

        Like likeToSave = likeMapper.toEntity(dto);
        likeToSave.setPost(postOfLike);
        likeToSave.setUserId(userId);
        likeToSave.setCreatedAt(LocalDateTime.now());
        postOfLike.getLikes().add(likeToSave);

        likeRepository.save(likeToSave);
        postService.savePost(postOfLike);
    }

    public void addLikeToComment(long userId, LikeDto dto) {
        validateUserExistence(userId);
        Comment commentOfLike = commentValidator.validateCommentExistence(dto.getCommentId());
        likeValidator.validateLikeExistence(dto.getId());
        likeValidator.validateLikeWasNotPutToPost(dto);
        likeValidator.validateUserAddOnlyOneLikeToComment(dto.getCommentId(), userId);

        Like likeToSave = likeMapper.toEntity(dto);
        likeToSave.setComment(commentOfLike);
        likeToSave.setUserId(userId);
        likeToSave.setCreatedAt(LocalDateTime.now());
        commentOfLike.getLikes().add(likeToSave);

        likeRepository.save(likeToSave);
        commentService.saveComment(commentOfLike);
    }

    public void removeLikeFromPost(long userId, LikeDto dto) {
        validateUserExistence(userId);
        likeValidator.validateThisUserAddThisLike(userId, dto.getId());

        Like likeToRemove = likeValidator.validateLikeExistence(dto.getId());
        Post postOfLike = postValidator.validatePostExistence(dto.getPostId());
        postOfLike.getLikes().remove(likeToRemove);

        likeRepository.delete(likeToRemove);
        postService.savePost(postOfLike);
    }

    public void removeLikeFromComment(long userId, LikeDto dto) {
        validateUserExistence(userId);
        likeValidator.validateThisUserAddThisLike(userId, dto.getId());

        Like likeToRemove = likeValidator.validateLikeExistence(dto.getId());
        Comment commentOfLike = commentValidator.validateCommentExistence(dto.getCommentId());
        commentOfLike.getLikes().remove(likeToRemove);

        likeRepository.delete(likeToRemove);
        commentService.saveComment(commentOfLike);
    }

    private void validateUserExistence(long id) {
        userServiceClient.getUser(id);
    }
}
