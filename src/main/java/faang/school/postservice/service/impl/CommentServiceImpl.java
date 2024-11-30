package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.dto.CommentDto;
import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.model.event.application.CommentCommittedEvent;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.validator.comment.CommentServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentServiceValidator validator;
    private final CommentMapper mapper;
    private final UserServiceClient userServiceClient;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long userId) {
        validator.validatePostExist(commentDto.getPostId());
        validator.validateCommentContent(commentDto.getContent());
        Comment comment = mapper.mapToComment(commentDto);
        CommentDto savedCommentDto = mapper.mapToCommentDto(commentRepository.save(comment));
        applicationEventPublisher.publishEvent(new CommentCommittedEvent(savedCommentDto));
        return savedCommentDto;
    }

    @Override
    public List<CommentDto> getComment(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        List<Comment> commentsSorted = comments.stream()
                .sorted(Comparator.comparing(Comment::getUpdatedAt).reversed())
                .toList();
        return mapper.mapToCommentDto(commentsSorted);
    }

    @Override
    public void deleteComment(Long commentId) {
        validator.validateCommentExist(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto updateComment(Long commentId, CommentDto commentDto, Long userId) {
        validator.validateCommentExist(commentId);
        validator.validateCommentContent(commentDto.getContent());
        userServiceClient.getUser(userId);
        Comment comment = commentRepository.findById(commentId).orElseThrow(NoSuchElementException::new);
        comment.setContent(commentDto.getContent());
        return mapper.mapToCommentDto(commentRepository.save(comment));
    }

    @Override
    public int getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    @Override
    public List<CommentDto> getRecentComments(Long postId, int numberOfComments) {
        Pageable pageable = PageRequest.of(0, numberOfComments);
        List<Comment> comments = commentRepository.findRecentByPostId(postId, pageable);
        return mapper.mapToCommentDto(comments);
    }
}
