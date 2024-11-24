package faang.school.postservice.service;

import faang.school.postservice.model.dto.redis.cache.RedisUserDto;

public interface RedisUserService {
    void saveUserIfNotExists(RedisUserDto userDto);

    RedisUserDto getUser(Long userId);

    void saveUser(RedisUserDto redisUserDto);
}
