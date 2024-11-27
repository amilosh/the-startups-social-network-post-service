package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostMapperTest {

    @InjectMocks
    private PostMapperImpl postMapper;

    @Mock
    private CommentMapperImpl commentMapper;

    private static final long ID = 1L;
    private static final String CONTENT = "content";
    private static final LocalDateTime SCHEDULED_AT =
            LocalDateTime.of(2024, 10, 10, 10, 10);
    private Post post;

    @BeforeEach
    public void init() {
        post = Post.builder()
                .id(ID)
                .authorId(ID)
                .projectId(ID)
                .content(CONTENT)
                .scheduledAt(SCHEDULED_AT)
                .createdAt(SCHEDULED_AT)
                .build();
    }

    @Test
    @DisplayName("Success when mapping Post with likeCount to ResponseDto")
    public void whenToResponseDtoThenReturnPostResponseDto() {
        PostResponseDto result = postMapper.toResponseDto(post, 1);

        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertEquals(ID, result.getAuthorId());
        assertEquals(ID, result.getProjectId());
        assertEquals(CONTENT, result.getContent());
        assertEquals(SCHEDULED_AT, result.getScheduledAt());
        assertEquals(1, result.getLikeCount());
    }

    @Test
    @DisplayName("Success when mapping Post to ResponseDto")
    public void whenToDtoThenReturnPostResponseDto() {
        post.setLikes(List.of(Like.builder().build()));
        PostResponseDto result = postMapper.toDto(post);

        assertNotNull(result);
        assertEquals(ID, result.getId());
        assertEquals(ID, result.getAuthorId());
        assertEquals(ID, result.getProjectId());
        assertEquals(CONTENT, result.getContent());
        assertEquals(SCHEDULED_AT, result.getScheduledAt());
        assertEquals(1, result.getLikeCount());
    }

    @Test
    @DisplayName("Success when mapping Post to PostCacheDto")
    public void whenToCacheDtoThenReturnPostCacheDto() {
        post.setLikes(List.of(Like.builder().build()));
        post.setComments(List.of(Comment.builder().build()));
        PostCacheDto result = postMapper.toCacheDto(post);

        assertNotNull(result);
        assertEquals(ID, result.getPostId());
        assertEquals(ID, result.getAuthorId());
        assertEquals(ID, result.getProjectId());
        assertEquals(CONTENT, result.getContent());
        assertEquals(SCHEDULED_AT, result.getCreatedAt());
        assertEquals(1, result.getLikesCount());
        assertEquals(1, result.getComments().size());
    }
}