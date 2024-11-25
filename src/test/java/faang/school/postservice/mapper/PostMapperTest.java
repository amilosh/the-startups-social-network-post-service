package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostMapperTest {
    private PostMapper mapper = new PostMapperImpl();

    @Test
    public void testToDto() {
        Post post = new Post();
        post.setContent("This is a post");
        post.setId(1L);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        PostDto dto = mapper.toDto(post);

        assertEquals(post.getId(), dto.id());
        assertEquals(post.getContent(), dto.content());
        assertEquals(dto.published(), post.isPublished());
        assertEquals(dto.publishedAt(), post.getPublishedAt());
    }

    @Test
    public void testToEntity() {
        PostDto dto = PostDto.builder()
                .content("This is a post")
                .id(1L)
                .published(true)
                .publishedAt(LocalDateTime.now())
                .build();

        Post post = mapper.toEntity(dto);

        assertEquals(post.getContent(), dto.content());
        assertEquals(post.getId(), dto.id());
        assertEquals(post.isPublished(), dto.published());
        assertEquals(post.getPublishedAt(), dto.publishedAt());
    }
}
