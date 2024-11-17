package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment getCommentById(long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment is not found"));
    }

    public boolean isCommentNotExist(long commentId){
        return  !commentRepository.existsById(commentId);
    }
}
