package faang.school.postservice.model.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class CommentEventKafka {
    private long postId;
    private long authorId;
    LocalDateTime createdAt;
}
