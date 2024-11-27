package faang.school.postservice.model.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(timeToLive = 60 * 60 * 24, value = "Post")
public class PostCache implements Serializable {
    @Id
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private Long likes;
    private Set<CommentCache> comments;
    private Long views;
}
