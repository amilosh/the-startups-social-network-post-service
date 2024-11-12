package faang.school.postservice.validator.like_validator;


import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeValidatorTest {

    @InjectMocks
    private LikeValidator likeValidator;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Test
    public void testValidateCommentHatLikeNotLike() {
        when(likeRepository.findByCommentIdAndUserId(1L, 1L))
                .thenReturn(Optional.empty());
        assertTrue(likeValidator.validateCommentHatLike(1L, 1L));
    }

    @Test
    public void testValidateCommentHat() {
        when(likeRepository.findByCommentIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(new Like()));
        assertFalse(likeValidator.validateCommentHatLike(1L, 1L));
    }

    @Test
    public void testValidatePostHatLike() {
        when(likeRepository.findByPostIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(new Like()));
        assertFalse(likeValidator.validatePostHatLike(1L, 1L));
    }

    @Test
    public void testValidatePostHatLikeNotLike() {
        when(likeRepository.findByPostIdAndUserId(1L, 1L))
                .thenReturn(Optional.empty());
        assertTrue(likeValidator.validatePostHatLike(1L, 1L));
    }

}
