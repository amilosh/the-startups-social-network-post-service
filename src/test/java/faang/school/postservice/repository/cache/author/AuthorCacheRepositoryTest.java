package faang.school.postservice.repository.cache.author;

import faang.school.postservice.dto.cache.author.EventAuthorDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorCacheRepositoryTest {

    @Mock
    private AuthorCacheRepository authorCacheRepository;

    @Test
    public void whenEventAuthorDtoPassedThenSaveItToRedis() {
        EventAuthorDto eventAuthorDto = EventAuthorDto.builder()
                .authorId(5L)
                .followers(List.of(10L, 20L, 30L))
                .build();

        when(authorCacheRepository.save(eventAuthorDto)).thenAnswer(invocationOnMock -> null);
        when(authorCacheRepository.findById(5L)).thenReturn(Optional.of(eventAuthorDto));

        authorCacheRepository.save(eventAuthorDto);
        Optional<EventAuthorDto> resultDto = authorCacheRepository.findById(5L);

        if (resultDto.isEmpty()) {
            fail();
        }
        assertEquals(resultDto.get().getAuthorId(), 5L);
        assertEquals(resultDto.get().getFollowers().size(), 3);
        assertEquals(resultDto.get().getFollowers().get(2), 30L);
    }
}
