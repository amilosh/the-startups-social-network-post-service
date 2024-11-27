package faang.school.postservice.event.kafka.like;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeKafkaEvent {

    @JsonProperty("comment_author_id")
    private Long commentAuthorId;
    @JsonProperty("like_author_id")
    private Long likeAuthorId;
    @JsonProperty("comment_id")
    private Long commentId;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
