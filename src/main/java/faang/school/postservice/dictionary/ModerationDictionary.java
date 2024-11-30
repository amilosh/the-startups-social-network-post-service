package faang.school.postservice.dictionary;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Component
@Slf4j
public class ModerationDictionary {
    private Set<String> forbiddenWords;

    @PostConstruct
    public void init() {
        forbiddenWords = new HashSet<>();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("moderation-words/bad-words.txt")) {
            if (inputStream == null) {
                log.error("Resource 'moderation-words/moderation-words.txt' not found in the classpath");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                forbiddenWords = reader.lines().collect(toSet());
                log.info("Loaded forbidden words: {}", forbiddenWords);
            }

        } catch (IOException e) {
            log.error("Error reading file", e);
        }
    }

    public boolean containsForbiddenWord(String text) {
        for (String word : forbiddenWords) {
            if (text.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
