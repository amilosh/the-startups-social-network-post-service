package faang.school.postservice.model.redis;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Set;

@RedisHash("post")
@Data
public class PostRedis implements Serializable {
    private String title;
    private String content;
    private long authorId;
    private Set<LikeRedis> likes;
}
