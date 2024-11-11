package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.AcceptanceLikeDto;
import faang.school.postservice.dto.like.ReturnLikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeMapper likeMapper;


    public ReturnLikeDto postLike(AcceptanceLikeDto acceptanceLikeDto, long postId) {
        Long userId = acceptanceLikeDto.getUserId();
        validateUserId(userId);
        Post post = getPost(postId);
        boolean result = validatePostHatLike(post.getId(), userId);
        if (!result) {
            throw new DataValidationException("Post already liked with id " + userId);
        }
        Like like = likeMapper.toLike(acceptanceLikeDto);
        like.setPost(post);
        likeRepository.save(like);
        post.getLikes().add(like);
        postRepository.save(post);
        return likeMapper.toReturnLikeDto(like);
    }

    public void deleteLikeFromPost(@RequestBody AcceptanceLikeDto likeDto, @PathVariable long postId) {
        Long userId = likeDto.getUserId();
        validateUserId(userId);
        Post post = getPost(postId);
        boolean result = validatePostHatLike(post.getId(), userId);
        if (result) {
            throw new DataValidationException("Post hat not liked with id " + userId);
        }
        likeRepository.deleteByPostIdAndUserId(postId, userId);

    }

    public ReturnLikeDto commentLike(AcceptanceLikeDto acceptanceLikeDto, long commentId) {
        Long userId = acceptanceLikeDto.getUserId();
        validateUserId(userId);
        Comment comment = getComment(commentId);
        boolean result = validateCommentHatLike(comment.getId(), userId);
        if (!result) {
            throw new DataValidationException("Comment already liked with id " + commentId);
        }
        Like like = likeMapper.toLike(acceptanceLikeDto);
        like.setComment(comment);
        likeRepository.save(like);
        comment.getLikes().add(like);
        commentRepository.save(comment);
        return likeMapper.toReturnLikeDto(like);
    }

    public void deleteLikeFromComment(AcceptanceLikeDto acceptanceLikeDto, long commentId) {
        Long userId = acceptanceLikeDto.getUserId();
        validateUserId(userId);
        Comment comment = getComment(commentId);
        boolean result = validateCommentHatLike(comment.getId(), userId);
        if (result) {
            throw new DataValidationException("Comment not liked with id " + commentId);
        }
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    private void validateUserId(Long userId) {
        userServiceClient.getUser(userId);
    }

    private Post getPost(long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new DataValidationException("Not found post with id " + postId);
        }
        return post.get();
    }

    private Comment getComment(long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new DataValidationException("Not found comment with id " + commentId);
        }
        return comment.get();
    }

    private boolean validatePostHatLike(long postId, long userId) {
        Optional<Like> like = likeRepository.findByPostIdAndUserId(postId, userId);
        return like.isEmpty();
    }

    private boolean validateCommentHatLike(long commentId, long userId) {
        Optional<Like> like = likeRepository.findByCommentIdAndUserId(commentId, userId);
        return like.isEmpty();
    }
}
