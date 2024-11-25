package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
@RedisHash(value = "users")
public class UserRedis implements Serializable {
    @Id
    private Long id;
    private String username;
}
