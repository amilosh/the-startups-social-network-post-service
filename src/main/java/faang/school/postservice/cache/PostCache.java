package faang.school.postservice.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "Post")
public class PostCache implements Serializable {

    @Id
    private Long Id;
    private String content;
    private Long authorId;
    private LocalDateTime publishedAt;

    @TimeToLive
    private Long ttl;
}
