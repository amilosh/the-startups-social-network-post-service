package faang.school.postservice.dto.redis.event;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class CommentEvent {
    private long id;
    private long authorId;
    private long postId;
    private long postAuthorId;
    private String content;
    private LocalDateTime timestamp;
}
