package faang.school.postservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "CommentRedisEntity", timeToLive = 60 * 24)
public class CommentRedisEntity implements Serializable {
    @Id
    private Long id;
    private String content;
    private Integer likes;
    private long authorId;
}
