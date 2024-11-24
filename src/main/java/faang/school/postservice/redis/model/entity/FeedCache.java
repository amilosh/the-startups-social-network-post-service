package faang.school.postservice.redis.model.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.TreeSet;

@RedisHash(value = "feeds")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class FeedCache implements Serializable {
    @Id
    private Long id;
    private TreeSet<Long> postIds;
}
