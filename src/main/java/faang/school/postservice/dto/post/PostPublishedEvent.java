package faang.school.postservice.dto.post;

import lombok.Data;

import java.util.List;

@Data
public class PostPublishedEvent {
    private long postId;
    private List<Long> subscribersIds;
}
