package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
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
    void validateHashtag_shouldNotThrowForValidHashtag() {
        String validHashtag = "#ValidHashtag";

        assertDoesNotThrow(() -> hashtagValidator.validateHashtag(validHashtag));
    }

    @Test
    void validateHashtag_shouldThrowForInvalidHashtag() {
        String invalidHashtag = "InvalidHashtag";

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> hashtagValidator.validateHashtag(invalidHashtag)
        );

        assertEquals("Hashtag must start with #", exception.getMessage());
    }
}