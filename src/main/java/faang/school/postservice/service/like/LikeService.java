package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.post.PostServiceImpl;
import faang.school.postservice.validator.like.LikeValidator;
import faang.school.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final LikeValidator likeValidator;
    private final UserServiceClient userServiceClient;
    private final PostValidator postValidator;
    private final LikeMapper likeMapper;
    private final PostServiceImpl postService;

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
        // is author exists
        validateUserExistence(userId);
        //validate is exists post
        Post postOfLike = postValidator.validatePostExistence(dto.getPostId());
        //validate is Like exists
        likeValidator.validateLikeExistence(dto.getId());
        //validate is this like wasn't added to comment
        likeValidator.validateLikeWasNotPutToComment(dto);
        //the same user cant put several likes to one post/comment
        likeValidator.validateUserAddOnlyOneLikeToPost(dto.getPostId());
    }

    private void validateUserExistence(long id) {
        userServiceClient.getUser(id);
    }
}
