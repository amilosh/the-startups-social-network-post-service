package faang.school.postservice.model.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "author", timeToLive = 60 * 60 * 24)
public class AuthorCache {
    @Id
    private long id;
    private Set<Long> postIds;
}
