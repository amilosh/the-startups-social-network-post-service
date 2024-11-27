package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.FeedComment;
import faang.school.postservice.dto.user.FeedUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FeedPost {
    private Long postId;
    private FeedUser author;
    private String content;
    private LocalDateTime publishedAt;
    private List<String> resourceKeys;
    private Long likeCount;
    private Long viewCount;
    private Long commentsCount;
    private List<FeedComment> comments;
}
