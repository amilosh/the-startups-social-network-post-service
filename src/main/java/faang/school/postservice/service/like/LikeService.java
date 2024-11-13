package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.AcceptanceLikeDto;
import faang.school.postservice.dto.like.ReturnLikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.like_validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeMapper likeMapper;
    private final LikeValidator validator;


    public ReturnLikeDto postLike(AcceptanceLikeDto acceptanceLikeDto, long postId) {
        Long userId = acceptanceLikeDto.getUserId();
        validator.validateUserId(userId);
        Post post = getPost(postId);
        boolean result = validator.validatePostHatLike(post.getId(), userId);
        if (!result) {
            throw new DataValidationException("Post already liked with id " + userId);
        }
        Like like = likeMapper.toLike(acceptanceLikeDto);
        like.setPost(post);
        likeRepository.save(like);
        log.info("The like {} was successfully saved in DB", like.getId());
        post.getLikes().add(like);
        postRepository.save(post);
        log.info("The post {} was successfully saved in DB", post.getId());
        return likeMapper.toReturnLikeDto(like);
    }

    public void deleteLikeFromPost(@RequestBody AcceptanceLikeDto acceptanceLikeDto, @PathVariable long postId) {
        Long userId = acceptanceLikeDto.getUserId();
        validator.validateUserId(userId);
        Post post = getPost(postId);
        boolean result = validator.validatePostHatLike(post.getId(), userId);
        if (result) {
            throw new DataValidationException("Post hat not liked with id " + userId);
        }
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("The like {} was successfully deleted from DB", acceptanceLikeDto.getId());
        deleteLike(post.getLikes(), userId);
        postRepository.save(post);
        log.info("The post {} was successfully saved in DB", post.getId());

    }

    public ReturnLikeDto commentLike(AcceptanceLikeDto acceptanceLikeDto, long commentId) {
        Long userId = acceptanceLikeDto.getUserId();
        validator.validateUserId(userId);
        Comment comment = getComment(commentId);
        boolean result = validator.validateCommentHatLike(comment.getId(), userId);
        if (!result) {
            throw new DataValidationException("Comment already liked with id " + commentId);
        }
        Like like = likeMapper.toLike(acceptanceLikeDto);
        like.setComment(comment);
        likeRepository.save(like);
        log.info("The like {} was successfully saved in DB", like.getId());
        comment.getLikes().add(like);
        commentRepository.save(comment);
        log.info("The comment {} was successfully saved in DB", comment.getId());
        return likeMapper.toReturnLikeDto(like);
    }

    public void deleteLikeFromComment(AcceptanceLikeDto acceptanceLikeDto, long commentId) {
        Long userId = acceptanceLikeDto.getUserId();
        validator.validateUserId(userId);
        Comment comment = getComment(commentId);
        boolean result = validator.validateCommentHatLike(comment.getId(), userId);
        if (result) {
            throw new DataValidationException("Comment not liked with id " + commentId);
        }
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("The like {} was successfully deleted from DB", acceptanceLikeDto.getId());
        deleteLike(comment.getLikes(), userId);
        commentRepository.save(comment);
        log.info("The comment {} was successfully saved in DB", comment.getId());
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

    private void deleteLike(List<Like> likes, long userId) {
        likes.removeIf(like -> like.getUserId().equals(userId));
    }
}
