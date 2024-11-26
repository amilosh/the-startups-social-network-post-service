package faang.school.postservice.model.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "post", timeToLive = 60 * 24 * 60)
public class PostRedis implements Serializable {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Integer likes;
    private Integer views;
    private List<Long> comments;
    private LocalDateTime publishedAt;
}
