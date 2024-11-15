package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDtoInput;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {
    private static final Long POST_ID = 1L;
    private static final Long LIKE_ID = 2L;
    private static final Long AUTHOR_ID = 3L;
    private static final Long COMMENT_ID = 4L;

    private CommentInputMapperImpl commentMapper;
    private Comment comment;
    private CommentDtoInput commentDto;

    @BeforeEach
    void setUp() {
        commentMapper = new CommentInputMapperImpl();

        Post post = new Post();
        post.setId(POST_ID);

        Like like = new Like();
        like.setId(LIKE_ID);

        comment = new Comment();
        comment.setId(COMMENT_ID);
        comment.setAuthorId(AUTHOR_ID);
        comment.setPost(post);
        comment.setContent("content");
        comment.setLikes(List.of(like));

        commentDto = new CommentDtoInput();
        commentDto.setId(COMMENT_ID);
        commentDto.setAuthorId(AUTHOR_ID);
        commentDto.setContent("content");
        commentDto.setPostId(POST_ID);
        commentDto.setLikeIds(List.of(LIKE_ID));
    }

    @Test
    void toCommentDto() {
        CommentDtoInput commentDtoInTest = commentMapper.toDto(comment);

        assertNotNull(commentDtoInTest);
        assertEquals(comment.getId(), commentDtoInTest.getId());
        assertEquals(comment.getAuthorId(), commentDtoInTest.getAuthorId());
        assertEquals(comment.getContent(), commentDtoInTest.getContent());
        assertEquals(LIKE_ID, commentDtoInTest.getLikeIds().get(0));
        assertEquals(POST_ID, commentDtoInTest.getPostId());
    }

    @Test
    void toComment() {
        Comment commentInTest = commentMapper.toEntity(commentDto);

        assertNotNull(commentInTest);
        assertEquals(commentDto.getId(), commentInTest.getId());
        assertEquals(commentDto.getAuthorId(), commentInTest.getAuthorId());
        assertEquals(commentDto.getContent(), commentInTest.getContent());
        assertEquals(commentDto.getLikeIds().get(0), commentInTest.getLikes().get(0).getId());
        assertEquals(commentDto.getPostId(), commentInTest.getPost().getId());
    }
}