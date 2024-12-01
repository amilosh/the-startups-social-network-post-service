package faang.school.postservice.dto.redis.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("post")
public class PostCacheDto implements Serializable {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private long likesCount;
    private Set<Long> likes;
    private long commentsCount;
    private Set<Long> comments;
}
