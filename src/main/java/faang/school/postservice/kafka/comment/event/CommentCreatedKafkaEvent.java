package faang.school.postservice.kafka.comment.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreatedKafkaEvent {
    private Long postId;
    private Long commentId;
    private String content;
    private long authorId;
}
