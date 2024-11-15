package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;

    public CommentDto createComment(CommentDto commentDto) {
        postService.getPostById(commentDto.getPostId());
        commentValidator.isAuthorExist(commentDto.getAuthorId());
        commentDto.setCreatedAt(LocalDateTime.now());
        Comment result = commentRepository.save(commentMapper.toEntity(commentDto));
        return commentMapper.toDto(result);
    }

    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        Comment currentComment = commentValidator.getExistingComment(commentId);
        currentComment.setUpdatedAt(LocalDateTime.now());
        currentComment.setContent(commentDto.getContent());
        commentRepository.save(currentComment);
        return commentMapper.toDto(currentComment);
    }

    public List<CommentDto> getAllComments(Long postId) {
        postService.getPostById(postId);
        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toDto)
                .toList();
    }

    public void deleteComment(Long authorId, Long commentId) {
        commentValidator.isAuthorExist(authorId);
        if (commentValidator.getExistingComment(commentId).getAuthorId() == commentId) {
            commentRepository.deleteById(commentId);
        }
    }

}

