package faang.school.postservice.config.dictionary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@PropertySource("classpath:application.properties")
class OffensiveWordsDictionaryTest {

    private OffensiveWordsDictionary offensiveWordsDictionary;

    private final String goodWord = "TEST";
    private final String badWordOne = "TEST1";
    private final String badWordTwo = "TEST2";

    private final List<String> initialWords = List.of(badWordOne, badWordTwo);

    @BeforeEach
    public void setUp() {
        offensiveWordsDictionary = new OffensiveWordsDictionary(initialWords);
    }

    @Test
    @DisplayName("If word contains in dictionary then return true")
    public void whenWordContainsInDictionaryThenReturnTrue() {
        assertTrue(offensiveWordsDictionary.isWordContainsInDictionary(badWordOne));
    }

    @Test
    @DisplayName("If word not contains in dictionary then return false")
    public void whenWordContainsInDictionaryThenReturnFalse() {
        assertFalse(offensiveWordsDictionary.isWordContainsInDictionary(goodWord));
    }

    @Test
    @DisplayName("When add new words in dictionary then they contains in dictionary")
    public void whenAddNewWordThenTheyAddCorrectly() {
        String word = "WORD";
        String anotherWord = "ANOTHER";
        String wordNotContains = "NOT";

        offensiveWordsDictionary.addWordsToDictionary(List.of(word, anotherWord));
        assertFalse(offensiveWordsDictionary.isWordContainsInDictionary(wordNotContains));
        assertTrue(offensiveWordsDictionary.isWordContainsInDictionary(badWordOne));
        assertTrue(offensiveWordsDictionary.isWordContainsInDictionary(badWordTwo));
        assertTrue(offensiveWordsDictionary.isWordContainsInDictionary(word));
        assertTrue(offensiveWordsDictionary.isWordContainsInDictionary(anotherWord));
    }
}
