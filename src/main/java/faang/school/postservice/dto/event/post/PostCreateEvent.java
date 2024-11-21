package faang.school.postservice.dto.event.post;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;


@Getter
@ToString
@Builder
public class PostCreateEvent {
    private Long postId;
    private Long authorId;
    private Long userId;
    private List<Long> subscribers;
}
