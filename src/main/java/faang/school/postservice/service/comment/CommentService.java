package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.RequestCommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.RequestCommentUpdateDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;
    private final CommentMapper commentMapper;

    public ResponseCommentDto createComment(RequestCommentDto requestCommentDto) {
        commentValidator.validateAuthorExists(requestCommentDto);
        commentValidator.validatePostExists(requestCommentDto.getPostId());

        Comment comment = commentMapper.toEntity(requestCommentDto);
        comment.setLikes(new ArrayList<>());

        commentRepository.save(comment);
        log.info("New comment with id: {} created", comment.getId());
        return commentMapper.toDto(comment);
    }

    public ResponseCommentDto updateComment(RequestCommentUpdateDto requestCommentUpdateDto) {
        commentValidator.validateCommentExists(requestCommentUpdateDto.getCommentId());
        Comment commentToUpdate = commentRepository.getCommentById(requestCommentUpdateDto.getCommentId());

        String postContent = requestCommentUpdateDto.getContent();
        commentToUpdate.setContent(postContent);

        commentRepository.save(commentToUpdate);
        log.info("Comment with id: {} updated", requestCommentUpdateDto.getCommentId());
        return commentMapper.toDto(commentToUpdate);
    }

    public List<ResponseCommentDto> getCommentsByPostId(Long postId) {
        commentValidator.validatePostExists(postId);

        List<Comment> commentsByPostId = commentRepository.findAllByPostId(postId);
        commentsByPostId.sort(Comparator.comparing(Comment::getCreatedAt).reversed());

        log.info("Retrieved all the comments for the post with id: {}", postId);
        return commentMapper.toDto(commentsByPostId);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
        log.info("Comment with id: {} deleted", commentId);
    }
}