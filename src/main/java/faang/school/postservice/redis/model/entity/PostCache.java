package faang.school.postservice.redis.model.entity;

import faang.school.postservice.redis.model.dto.CommentRedisDto;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArraySet;

@RedisHash(value = "posts")
@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class PostCache implements Serializable {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private int numberOfLikes;
    private int numberOfViews;
    private CopyOnWriteArraySet<CommentRedisDto> comments;
    private Long version;
}
