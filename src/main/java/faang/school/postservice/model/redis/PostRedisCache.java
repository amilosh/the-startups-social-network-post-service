package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NavigableSet;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("posts")
public class PostRedisCache implements Serializable {

    @Id
    private long id;
    @TimeToLive
    private int ttl;
    @Version
    private long version;

    @Reference
    @ToString.Exclude
    private NavigableSet<CommentRedisCache> commentRedisCaches;
    @Reference
    @ToString.Exclude
    private AuthorRedisCache author;

    private String content;
    private List<String> resourceIds;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private int likesCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostRedisCache that = (PostRedisCache) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getId());
    }
}
