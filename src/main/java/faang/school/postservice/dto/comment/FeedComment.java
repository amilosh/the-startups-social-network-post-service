package faang.school.postservice.dto.comment;

import faang.school.postservice.cache.UserCache;
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
    private UserCache author;
    private String content;
    private LocalDateTime createdAt;
}