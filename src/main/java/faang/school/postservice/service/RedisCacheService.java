package faang.school.postservice.service;

import faang.school.postservice.model.dto.user.UserDto;
import faang.school.postservice.model.redis.PostRedis;

public interface RedisCacheService {
    void savePost(PostRedis postRedis);
    void saveUser(UserDto userDto);
}
