package faang.school.postservice.service;

import faang.school.postservice.model.Hashtag;
import faang.school.postservice.repository.HashtagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashtagServiceTest {
    @Mock
    private HashtagRepository hashtagRepository;

    @InjectMocks
    private HashtagService hashtagService;

    @Test
    void shouldCreateAndSaveNewHashtagWhenNotExists() {
        String tag = "example";
        Hashtag newHashtag = Hashtag.builder()
                .tag(tag)
                .createdAt(LocalDateTime.now())
                .build();

        when(hashtagRepository.findByTag(tag)).thenReturn(Optional.empty());
        when(hashtagRepository.save(any(Hashtag.class))).thenReturn(newHashtag);

        Hashtag result = hashtagService.create(tag);

        assertEquals(newHashtag.getTag(), result.getTag());
        verify(hashtagRepository, times(1)).save(any(Hashtag.class));
    }

    @Test
    void shouldReturnExistingHashtagWhenExists() {
        String tag = "existing";
        Hashtag existingHashtag = Hashtag.builder()
                .tag(tag)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        when(hashtagRepository.findByTag(tag)).thenReturn(Optional.of(existingHashtag));

        Hashtag result = hashtagService.create(tag);

        assertEquals(existingHashtag, result);
        verify(hashtagRepository, never()).save(any(Hashtag.class));
    }

    @Test
    void shouldReturnHashtagWhenExists() {
        String tag = "example";
        Hashtag existingHashtag = Hashtag.builder().tag(tag).build();

        when(hashtagRepository.findByTag(tag)).thenReturn(Optional.of(existingHashtag));

        Optional<Hashtag> result = hashtagService.findByTag(tag);

        assertEquals(Optional.of(existingHashtag), result);
        verify(hashtagRepository, times(1)).findByTag(tag);
    }

    @Test
    void shouldReturnEmptyWhenHashtagNotExists() {
        String tag = "nonexistent";
        when(hashtagRepository.findByTag(tag)).thenReturn(Optional.empty());

        Optional<Hashtag> result = hashtagService.findByTag(tag);

        assertEquals(Optional.empty(), result);
        verify(hashtagRepository, times(1)).findByTag(tag);
    }
}