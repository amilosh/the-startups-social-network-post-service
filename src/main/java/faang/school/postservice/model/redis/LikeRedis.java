package faang.school.postservice.model.redis;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("like")
@Data
public class LikeRedis {
    private long id;
    private long likeAuthorId;
}
