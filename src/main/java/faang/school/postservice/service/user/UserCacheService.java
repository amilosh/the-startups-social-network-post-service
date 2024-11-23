package faang.school.postservice.service.user;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.repository.redis.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserCacheService {
    private final UserServiceClient userServiceClient;
    private final UserRedisRepository userRedisRepository;

    public void saveUserToRedisRepository(long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        UserRedis userRedis = new UserRedis(userDto.getId(), userDto.getUsername());
        log.info("Save user with ID: {} to Redis", userId);
        userRedisRepository.save(userRedis);
    }

    public void saveAllToRedisRepository(List<Long> userIds) {
        List<UserDto> userDtos = userServiceClient.getUsersByIds(userIds);
        List<UserRedis> userRedisList = userDtos.stream()
                .map(dto -> new UserRedis(dto.getId(), dto.getUsername()))
                .toList();
        userRedisRepository.saveAll(userRedisList);
    }
}
