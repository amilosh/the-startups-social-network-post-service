package faang.school.postservice.dto.comment;

import faang.school.postservice.dto.user.FeedUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FeedComment {
    private Long commentId;
    private FeedUser author;
    private String content;
    private LocalDateTime createdAt;
}
