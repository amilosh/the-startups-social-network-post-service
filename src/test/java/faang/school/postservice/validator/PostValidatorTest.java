package faang.school.postservice.validator;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exceptions.EntityNotFoundException;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostValidator postValidator;

    PostDto postDto;

    @BeforeEach
    void setUp() {
        postDto = new PostDto();
        postDto.setId(1L);
    }

    @Test
    public void testCheckPostExistenceWherPostExist() {
        when(postRepository.existsById(postDto.getId())).thenReturn(true);
        assertDoesNotThrow(() -> postValidator.checkPostExistence(postDto),
                "No exception should be thrown if post exists");
        verify(postRepository, times(1)).existsById(postDto.getId());
    }

    @Test
    public void testCheckPostExistenceWherPostNotFound() {
        when(postRepository.existsById(postDto.getId())).thenReturn(false);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            postValidator.checkPostExistence(postDto);
        });
        verify(postRepository, times(1)).existsById(postDto.getId());
    }
}
