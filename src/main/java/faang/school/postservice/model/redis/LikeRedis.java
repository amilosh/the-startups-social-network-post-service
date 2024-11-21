package faang.school.postservice.model.redis;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("like")
@Data
public class LikeRedis implements Serializable {
    private long id;
    private long likeAuthorId;
}
