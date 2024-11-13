package faang.school.postservice.service.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment findComment(Long id){
        return commentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
