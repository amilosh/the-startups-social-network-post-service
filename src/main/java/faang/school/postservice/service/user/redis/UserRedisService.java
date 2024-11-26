package faang.school.postservice.service.user.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.model.user.UserRedis;
import faang.school.postservice.repository.redis.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserRedisService {
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

    public void savePostsAuthorsToRedis(List<Post> posts) {
        Set<Long> authorIds = posts.stream()
                .map(Post::getAuthorId)
                .collect(Collectors.toSet());

        Set<Long> missingAuthorIds = defineMissingAuthorIdsInRedis(authorIds);
        List<UserDto> userDtos = userServiceClient.getUsersByIds(new ArrayList<>(missingAuthorIds));

        List<UserRedis> userRedisList = userDtos.stream()
                .map(dto -> new UserRedis(dto.getId(), dto.getUsername()))
                .toList();
        userRedisRepository.saveAll(userRedisList);
    }

    private Set<Long> defineMissingAuthorIdsInRedis(Set<Long> authorIds) {
        Iterable<UserRedis> iterableUsers = userRedisRepository.findAllById(authorIds);
        Set<Long> existingAuthorIds = StreamSupport.stream(iterableUsers.spliterator(), false)
                .map(UserRedis::getId)
                .collect(Collectors.toSet());
        return authorIds.stream()
                .filter(id -> !existingAuthorIds.contains(id))
                .collect(Collectors.toSet());
    }
}
