package faang.school.postservice.redis.model.entity;

import faang.school.postservice.redis.model.dto.CommentRedisDto;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.TreeSet;

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
    private TreeSet<CommentRedisDto> comments;


}
