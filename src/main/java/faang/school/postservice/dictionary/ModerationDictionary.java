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

@Component
@Slf4j
public class ModerationDictionary {
    private Set<String> forbiddenWords;

    @PostConstruct
    public void init() {
        forbiddenWords = new HashSet<>();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("moderation-words/moderation-words.txt")) {
            if (inputStream == null) {
                log.error("Resource 'moderation-words/moderation-words.txt' not found in the classpath");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                forbiddenWords = reader.lines().collect(Collectors.toSet());
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
