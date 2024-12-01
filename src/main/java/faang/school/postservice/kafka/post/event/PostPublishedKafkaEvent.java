package faang.school.postservice.kafka.post.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostPublishedKafkaEvent {
    private Long postId;
    private List<Long> followerIds;
    private LocalDateTime publishedAt;
}
