package faang.school.postservice.event.kafka.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreatedEvent {

    @JsonProperty("comment_id")
    private Long commentId;
    @JsonProperty("author_id")
    private Long authorId;
    @JsonProperty("subscribers")
    private List<Long> subscribers;
}
