package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class CommentsServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    private CommentService commentService;

    @Test
    void createComment_Success(){

    }
    @Test
    void updateComment_Success(){

    }
    @Test
    void getAllComments_Success(){

    }
    @Test
    void deleteComment_Success(){

    }
    private CommentDto creatTestCommentDto(){
        return CommentDto.builder()
                .id(1L)
                .content("Test comment")
                .authorId(2L)
                .postId(100L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    private Comment creatTestComment(){
        return Comment.builder()
                .id(1L)
                .content("Test comment")
                .authorId(2L)

                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
