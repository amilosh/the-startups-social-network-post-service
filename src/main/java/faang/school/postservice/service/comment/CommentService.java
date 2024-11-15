package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDtoInput;
import faang.school.postservice.dto.comment.CommentDtoOutput;
import faang.school.postservice.dto.comment.CommentDtoOutputUponUpdate;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.mapper.comment.CommentInputMapper;
import faang.school.postservice.mapper.comment.CommentOutputMapper;
import faang.school.postservice.mapper.comment.CommentOutputUponUpdateMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;
    private final CommentInputMapper commentInputMapper;
    private final CommentOutputMapper commentOutputMapper;
    private final CommentOutputUponUpdateMapper commentOutputUponUpdateMapper;

    public CommentDtoOutput createComment(CommentDtoInput commentDtoInput) {
        commentValidator.validateComment(commentDtoInput);
        commentValidator.validateAuthorExists(commentDtoInput);
        commentValidator.validatePostExists(commentDtoInput.getPostId());

        commentDtoInput.setId(null);
        Comment comment = commentInputMapper.toEntity(commentDtoInput);

        commentRepository.save(comment);
        log.info("New comment with id: {} created", comment.getId());
        return commentOutputMapper.toDto(comment);
    }

    public CommentDtoOutputUponUpdate updateComment(CommentUpdateDto commentUpdateDto) {
        commentValidator.validateCommentUpdateDto(commentUpdateDto);
        Comment commentToUpdate = commentRepository.getCommentById(commentUpdateDto.getCommentId());

        String postContent = commentUpdateDto.getContent();
        commentToUpdate.setContent(postContent);

        commentRepository.save(commentToUpdate);
        log.info("Comment with id: {} updated", commentUpdateDto.getCommentId());
        return commentOutputUponUpdateMapper.toDto(commentToUpdate);
    }

    public List<CommentDtoOutput> getCommentsByPostId(Long postId) {
        commentValidator.validatePostExists(postId);

        List<Comment> commentsByPostId = commentRepository.findAllByPostId(postId);
        commentsByPostId.sort(Comparator.comparing(Comment::getCreatedAt).reversed());

        log.info("Retrieved all the comments for the post with id: {}", postId);
        return commentOutputMapper.toDto(commentsByPostId);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
        log.info("Comment with id: {} deleted", commentId);
    }
}