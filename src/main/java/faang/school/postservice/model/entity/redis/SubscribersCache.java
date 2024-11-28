package faang.school.postservice.model.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "subscriber", timeToLive = 60 * 60 * 24)
public class SubscribersCache {
    @Id
    long userId;
    Set<Long> postIds;
}
