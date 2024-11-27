package faang.school.postservice.event.kafka.post.like;

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
public class PostLikeKafkaEvent {

    @JsonProperty("post_author_id")
    private Long postAuthorId;
    @JsonProperty("like_author_id")
    private Long likeAuthorId;
    @JsonProperty("post_id")
    private Long postId;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
