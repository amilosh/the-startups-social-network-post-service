package faang.school.postservice;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestAddComment {

    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void testAddComment() throws InterruptedException {
        CommentDto comment = CommentDto.builder()
                .content("content")
                .authorId(3L)
                .postId(344L)
                .build();

        commentService.createComment(commentMapper.toEntity(comment));

        Thread.sleep(5000);
    }

}
