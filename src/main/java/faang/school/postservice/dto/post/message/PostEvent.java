package faang.school.postservice.dto.post.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostEvent {
    private long postId;
    private String content;
    private long authorId;
    private long projectId;
    private int likeCount;
    private LocalDateTime scheduledAt;
    private List<Long> subscribers;
}
