package faang.school.postservice.model.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CommentEvent {
    private long postId;
    private long authorId;
}
