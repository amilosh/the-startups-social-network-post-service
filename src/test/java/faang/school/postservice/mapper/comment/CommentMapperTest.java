package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommentMapperTest {

    private CommentMapper commentMapper;

    @BeforeEach
    void setUp() {
        commentMapper = new CommentMapperImpl();
    }

    @Test
    void testToDto() {
        Post post = new Post();
        post.setId(1L);

        Like like1 = new Like();
        like1.setId(101L);
        Like like2 = new Like();
        like2.setId(102L);

        Comment comment = Comment.builder()
                .id(1L)
                .content("Test comment")
                .authorId(123L)
                .likes(List.of(like1, like2))
                .post(post)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CommentResponseDto dto = commentMapper.toDto(comment);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test comment", dto.getContent());
        assertEquals(123L, dto.getAuthorId());
        assertEquals(1L, dto.getPostId());
        assertEquals(List.of(101L, 102L), dto.getLikeIds());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getUpdatedAt());
    }

    @Test
    void testToDtoList() {
        Post post = new Post();
        post.setId(1L);

        Comment comment1 = Comment.builder().id(1L).content("Comment 1").authorId(123L).post(post).build();
        Comment comment2 = Comment.builder().id(2L).content("Comment 2").authorId(456L).post(post).build();

        List<Comment> comments = List.of(comment1, comment2);

        List<CommentResponseDto> dtoList = commentMapper.toDto(comments);

        assertNotNull(dtoList);
        assertEquals(2, dtoList.size());
        assertEquals(1L, dtoList.get(0).getId());
        assertEquals(2L, dtoList.get(1).getId());
    }

    @Test
    void testToEntity() {
        CommentRequestDto commentDto = new CommentRequestDto();
        commentDto.setContent("New Comment");
        commentDto.setAuthorId(123L);
        commentDto.setPostId(1L);

        Comment comment = commentMapper.toEntity(commentDto);

        assertNotNull(comment);
        assertEquals("New Comment", comment.getContent());
        assertEquals(123L, comment.getAuthorId());
        assertNotNull(comment.getPost());
        assertEquals(1L, comment.getPost().getId());
    }

    @Test
    void testMapLikesToLikeIds() {
        Like like1 = new Like();
        like1.setId(101L);
        Like like2 = new Like();
        like2.setId(102L);

        List<Like> likes = List.of(like1, like2);

        List<Long> likeIds = commentMapper.mapLikesToLikeIds(likes);

        assertNotNull(likeIds);
        assertEquals(2, likeIds.size());
        assertTrue(likeIds.containsAll(List.of(101L, 102L)));
    }

    @Test
    void testMapLikesToLikeIds_NullLikes() {
        List<Long> likeIds = commentMapper.mapLikesToLikeIds(null);

        assertNull(likeIds);
    }
}