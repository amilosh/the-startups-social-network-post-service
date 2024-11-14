package faang.school.postservice.validator;

import faang.school.postservice.model.Hashtag;
import faang.school.postservice.service.HashtagService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashtagValidatorTest {
    @InjectMocks
    private HashtagValidator hashtagValidator;

    @Mock
    private HashtagService hashtagService;

    @Test
    void shouldThrowExceptionWhenHashtagNotFound() {
        String nonExistentHashtag = "#nonexistent";

        when(hashtagService.findByTag(nonExistentHashtag)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> hashtagValidator.validateHashtag(nonExistentHashtag));
    }

    @Test
    void shouldPassWhenHashtagExists() {
        String existingHashtag = "#existing";

        when(hashtagService.findByTag(existingHashtag)).thenReturn(Optional.of(Hashtag.builder().tag(existingHashtag).build()));

        hashtagValidator.validateHashtag(existingHashtag);
    }
}