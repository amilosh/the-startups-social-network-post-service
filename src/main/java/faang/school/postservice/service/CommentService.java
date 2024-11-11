package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CommentMapper commentMapper;
    private final PostValidator postValidator;
    private final UserServiceClient userServiceClient;


    public CommentDto create(long postId, CreateCommentDto dto) {
        postValidator.validatePostExistsById(postId);
        userServiceClient.getUser(dto.getAuthorId());

        Comment comment = commentMapper.toEntity(dto);
        comment.setPost(postService.getPostById(postId));
        commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }
}
