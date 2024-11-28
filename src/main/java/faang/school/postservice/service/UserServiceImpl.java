package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.cache.MultiGetCacheService;
import faang.school.postservice.service.cache.SingleCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserServiceClient userServiceClient;
    private final SingleCacheService<Long, UserDto> userCacheService;
    private final MultiGetCacheService<List<Long>, UserDto> userMultiGetCacheService;

    @Override
    public UserDto getUserFromCacheOrService(Long userId) {
        UserDto userDto = userCacheService.get(userId);
        return Objects.requireNonNullElseGet(userDto, () -> {
            log.warn("User with id {} not found", userId);
            return userServiceClient.getUser(userId);
        });
    }

    @Override
    public List<UserDto> getUsersFromCacheOrService(List<Long> userIds) {
        List<UserDto> userDtos = userMultiGetCacheService.getAll(userIds);
        if (userDtos.isEmpty()) {
            return userServiceClient.getUsersByIds(userIds);
        }
        return userDtos;
    }
}
