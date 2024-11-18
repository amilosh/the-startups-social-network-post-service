package faang.school.postservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "PostRedisEntity", timeToLive = 60 * 24)
public class PostRedisEntity implements Serializable {
    private Long id;
    private String content;
    private Long authorId;
    private Integer likes;
    private List<CommentRedisDto> comments;
}
