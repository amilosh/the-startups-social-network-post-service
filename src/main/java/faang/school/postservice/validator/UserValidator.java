package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserServiceClient userServiceClient;

    public void validateUserExists(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException ex) {
            throw new EntityNotFoundException("User does not exist");
        }
    }
}
