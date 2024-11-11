package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeValidator likeValidator;
    private final LikeRepository likeRepository;
    private final UserContext userContext;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeMapper likeMapper;

    public LikeDto getLikeById(long id) {
        return likeMapper.toDto(likeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Like with id %s not found", id)))
        );
    }

    public List<LikeDto> getAllLikes() {
        return likeRepository.findAll().stream()
                .map(likeMapper::toDto)
                .toList();
    }

    public LikeDto addLike(LikeDto likeDto) {
        long userId = userContext.getUserId();

        likeValidator.validateAdded(likeDto, userId,
                postService.existsPostById(likeDto.getPostId()),
                commentService.existsCommentById(likeDto.getCommentId()),
                existsLikeByCommentIdAndUserId(likeDto.getCommentId(), userId),
                existsLikeByPostIdAndUserId(likeDto.getPostId(), userId)
        );

        Like addedLike = likeMapper.toEntity(likeDto);
        addedLike.setUserId(userId);
        setLikeObject(likeDto, addedLike);
        addedLike.setCreatedAt(LocalDateTime.now());

        return likeMapper.toDto(likeRepository.save(addedLike));
    }

    public void deleteLike(LikeDto likeDto) {
        likeValidator.validateDeleted(existsLikeById(likeDto.getId()));
        likeRepository.deleteById(likeDto.getId());
    }

    private boolean existsLikeById(long id) {
        return likeRepository.existsById(id);
    }

    private boolean existsLikeByCommentIdAndUserId(Long commentId, long userId) {
        if (commentId == null) return false;
        return likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent();
    }

    private boolean existsLikeByPostIdAndUserId(Long postId, long userId) {
        if (postId == null) return false;
        return likeRepository.findByPostIdAndUserId(postId, userId).isPresent();
    }

    private void setLikeObject(LikeDto likeDto, Like like) {
        if (likeDto.getPostId() != null) {
            like.setPost(postService.getPost(likeDto.getPostId()));
        }

        if (likeDto.getCommentId() != null) {
            like.setComment(commentService.getComment(likeDto.getCommentId()));
        }
    }
}
