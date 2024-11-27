package faang.school.postservice.event.kafka.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostCreatedEvent {
    private Long postId;
    private Long authorId;
    private List<Long> subscribers;
}
