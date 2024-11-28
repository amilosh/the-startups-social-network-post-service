package faang.school.postservice.model.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikeEventKafka {
    private Long likeAuthorId;
    private Long postId;
    private LocalDateTime likeDate;
}
