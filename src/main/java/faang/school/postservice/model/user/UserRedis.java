package faang.school.postservice.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
@RedisHash
public class UserRedis implements Serializable {
    @Id
    private Long id;
    private String username;
}
