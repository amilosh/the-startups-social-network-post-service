package faang.school.postservice.service.user;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.repository.redis.PostRedisRepository;
import faang.school.postservice.repository.redis.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CacheService {
    private final UserServiceClient userServiceClient;
    private final UserRedisRepository userRedisRepository;
    private final PostRedisRepository postRedisRepository;
    private final PostMapper postMapper;

    public void saveAuthor(long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        UserRedis userRedis = new UserRedis(userDto.getId(), userDto.getUsername());
        log.info("Save user with ID: {} to Redis", userId);
        userRedisRepository.save(userRedis);
    }

    public void saveAllAuthors(List<Post> posts) {
        List<Long> userIds = posts.stream()
                .map(Post::getAuthorId)
                .toList();
        List<UserDto> users = userServiceClient.getUsersByIds(userIds);
        List<UserRedis> userRedisList = users.stream()
                .map(dto -> new UserRedis(dto.getId(), dto.getUsername()))
                .toList();
        userRedisRepository.saveAll(userRedisList);
    }

    public void savePost(Post post) {
        postRedisRepository.save(postMapper.toPostRedis(post));
    }

    public void saveAllPosts(List<Post> posts) {
        List<PostRedis> redisPosts = posts.stream()
                .map(postMapper::toPostRedis)
                .toList();
        postRedisRepository.saveAll(redisPosts);
    }
}
