package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PostMapperTest {

    @InjectMocks
    private PostMapperImpl postMapper;

    @Test
    void testToResponseDto_WhenArgsValid_ReturnValidPostResponseDto() {

        Post post = Post.builder()
                .id(1L)
                .content("content")
                .authorId(2L)
                .projectId(3L)
                .scheduledAt(LocalDateTime.of(2021, 7, 15, 10, 30))
                .build();

        int likeCount = 5;

        PostResponseDto postResponseDto = postMapper.toResponseDto(post, likeCount);

        assertEquals(postResponseDto.getId(), post.getId());
        assertEquals(postResponseDto.getContent(), post.getContent());
        assertEquals(postResponseDto.getAuthorId(), post.getAuthorId());
        assertEquals(postResponseDto.getProjectId(), post.getProjectId());
        assertEquals(postResponseDto.getScheduledAt(), post.getScheduledAt());
        assertEquals(postResponseDto.getLikeCount(), likeCount);
    }

    @Test
    void testToPost_WhenArgsValid_ReturnValidPost() {
        PostRequestDto postRequestDto = PostRequestDto.builder()
                .content("content")
                .authorId(2L)
                .projectId(3L)
                .build();

        Post post = postMapper.toPost(postRequestDto);

        assertEquals(post.getContent(), postRequestDto.getContent());
        assertEquals(post.getAuthorId(), postRequestDto.getAuthorId());
        assertEquals(post.getProjectId(), postRequestDto.getProjectId());
    }
}
