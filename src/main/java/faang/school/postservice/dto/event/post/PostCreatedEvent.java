package faang.school.postservice.dto.event.post;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class PostCreatedEvent {
    private final Long postId;
    private final Long authorId;
    private final List<Long> subscribers;
}
