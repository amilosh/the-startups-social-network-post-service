package faang.school.postservice.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentPublishMessage {
    private Long commentId;
    private Long postId;
    private Long commentAuthorId;
    private String content;
    private LocalDateTime createdAt;
}