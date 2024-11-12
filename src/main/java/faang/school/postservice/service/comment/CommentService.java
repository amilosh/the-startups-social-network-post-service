package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final CommentMapper commentMapper;

    public CommentDto createComment(CommentDto commentDto) {
        isPostExist(commentDto.getPostId());
        isAuthorExist(commentDto.getAuthorId());
        commentDto.setCreatedAt(LocalDateTime.now());
        commentRepository.save(commentMapper.toEntity(commentDto));
        return commentDto;
    }

    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        Comment currentComment = getExistingComment(commentId);
        currentComment.setUpdatedAt(LocalDateTime.now());
        currentComment.setContent(commentDto.getContent());
        commentRepository.save(currentComment);
        return commentMapper.toDto(currentComment);
    }

    public List<CommentDto> getAllComments(Long postId) {
        isPostExist(postId);
        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toDto)
                .toList();
    }

    public void deleteComment(Long authorId, Long commentId) {
        isAuthorExist(authorId);
        if (getExistingComment(commentId).getAuthorId() == commentId) {
            commentRepository.deleteById(commentId);
        }
    }

    private Comment getExistingComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException("Comment with id : " + commentId + "does not exist"));
    }

    private void isAuthorExist(Long authorId) {
        if (userServiceClient.getUser(authorId) == null) {
            throw new EntityNotFoundException("Author with ID " + authorId + " does not exist");
        }
    }

    private void isPostExist(Long postId) {
        postService.getPostById(postId).orElseThrow(() ->
                new EntityNotFoundException("Post with ID " + postId + " does not exist"));
    }
}

