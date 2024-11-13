package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.mapper.like.LikeCommentMapper;
import faang.school.postservice.mapper.like.LikePostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostServiceImpl postService;
    private final CommentService commentService;
    private final LikePostMapper likePostMapper;
    private final LikeCommentMapper likeCommentMapper;

    public LikePostDto likePost(LikePostDto likePostDto){
        Post post = postService.findPost(likePostDto.postId());
        post.getLikes()
                .forEach(like -> {
                    if(Objects.equals(like.getUserId(), likePostDto.userId())){
                        throw new IllegalArgumentException("This post was already liked by this user");
                    }
                });

        Like like = likePostMapper.toEntity(likePostDto);
        like.setPost(post);

        likeRepository.save(like);

        return likePostMapper.toDto(like);
    }

    public void unlikePost(Long likeId){
        Like like = likeRepository.findById(likeId).orElseThrow(EntityNotFoundException::new);

        likeRepository.deleteById(like.getId());

        log.info("Like of post was successfully deleted");
    }

    public LikeCommentDto likeComment(LikeCommentDto likeCommentDto){
        Post post = postService.findPost(likeCommentDto.postId());
        Comment comment = commentService.findComment(likeCommentDto.commentId());

        comment.getLikes()
                .forEach(like -> {
                    if(Objects.equals(like.getUserId(), likeCommentDto.userId())){
                        throw new IllegalArgumentException("This comment was already liked by this user");
                    }
                });

        Like like = likeCommentMapper.toEntity(likeCommentDto);
        like.setComment(comment);

        Like createdLike = likeRepository.save(like);

        return likeCommentMapper.toDto(createdLike, post);
    }

    public void unlikeComment(Long likeId){
        Like like = likeRepository.findById(likeId).orElseThrow(EntityNotFoundException::new);

        likeRepository.deleteById(like.getId());

        log.info("Like of comment was successfully deleted");
    }
}
