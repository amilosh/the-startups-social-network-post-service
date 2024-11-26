package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@RedisHash(value = "post")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRedis implements Serializable {
    @Id
    private long id;
    private String title;
    private String content;
    private long authorId;
    private String likeKey;
    private String commentKey;

    @TimeToLive
    private long timeToLive;
}
