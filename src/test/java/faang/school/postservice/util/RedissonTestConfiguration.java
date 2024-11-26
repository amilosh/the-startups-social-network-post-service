package faang.school.postservice.util;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@TestConfiguration
public class RedissonTestConfiguration {

    @Bean
    @Profile("test")
    public RedissonClient testRedissonClient(@Value("${spring.data.redis.host}") String host,
                                             @Value("${spring.data.redis.port}") int port) {

        Config config = new Config();
        config.useSingleServer()
                .setAddress(String.format("redis://%s:%d", host, port));

        return Redisson.create(config);
    }
}
