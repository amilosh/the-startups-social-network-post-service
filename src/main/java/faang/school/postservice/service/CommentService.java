package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostService postService;
    private final UserValidator userValidator;

    @Transactional
    public CommentDto addComment(long postId, CommentDto commentDto) {
        log.info("Trying to add comment: {} to post: {}", commentDto, postId);
        userValidator.validateUserExists(commentDto.authorId());

        Post post = postService.getById(postId);
        Comment comment = commentMapper.toEntity(commentDto);

        postService.addCommentToPost(post, comment);
        comment.setPost(post);

        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    @Transactional
    public CommentDto updateComment(long commentId, String content) {
        log.info("Trying to update comment: {} with the following content: {}",
                commentId, content);
        Comment comment = getById(commentId);
        comment.setContent(content);
        return commentMapper.toDto(comment);
    }

    @Transactional
    public List<CommentDto> getPostComments(long postId) {
        log.info("Trying to get comments of post: {}", postId);
        return commentMapper.toDto(commentRepository.findAllByPostIdSortedByDate(postId));
    }

    @Transactional
    public void deleteComment(long commentId) {
        log.info("Trying to delete comment: {commentId}");
        commentRepository.deleteById(commentId);
    }

    private Comment getById(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
    }
}
