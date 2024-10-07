package faang.school.postservice.service.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getUnverifiedComments() {
        return commentRepository.findByVerifiedAtIsNull();
    }

    public void setVerifyToComment(Comment comment, boolean isVerified) {
        comment.setVerified(isVerified);
        comment.setVerifiedAt(LocalDateTime.now());
    }

    public void saveComments(List<Comment> comments) {
        commentRepository.saveAll(comments);
    }

    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }
}
