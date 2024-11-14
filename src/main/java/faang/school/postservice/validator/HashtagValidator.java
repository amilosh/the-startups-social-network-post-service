package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.HashtagService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashtagValidator {
    private final HashtagService hashtagService;

    public void validateHashtag(String hashtag) {
        if (hashtagService.findByTag(hashtag).isEmpty()) {
            throw new EntityNotFoundException("Hashtag with name: " + hashtag + " not found");
        }

        if (!hashtag.startsWith("#")) {
            throw new DataValidationException("Hashtag must start with #");
        }
    }
}
