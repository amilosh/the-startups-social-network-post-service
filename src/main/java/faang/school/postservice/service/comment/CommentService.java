package faang.school.postservice.service.comment;


import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostServiceImpl postService;

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {

        return null;
    }

    @Transactional
    public CommentDto updateComment(long id, CommentDto commentDto) {

        return null;
    }

    @Transactional
    public List<CommentDto> getAllComments(long postId) {

        return null;
    }

    @Transactional
    public void deleteComment(long id, long commentId) {

        commentRepository.existsById(commentId);
    }
}
