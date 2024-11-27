package faang.school.postservice.dto.cache.feed;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Set;

@Setter
@Getter
@Builder
@ToString
@RedisHash(value = "Feed")
public class FeedCacheDto implements Serializable {
    @Id
    private Long subscriberId;
    private Set<Long> postsIds;
}
