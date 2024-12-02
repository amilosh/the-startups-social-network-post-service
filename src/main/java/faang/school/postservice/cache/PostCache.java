package faang.school.postservice.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PostCache implements Serializable {
    private Long id;
    private Long authorId;
    private String content;
    private LocalDateTime publishedAt;

    @Builder.Default
    private List<String> resourceKeys = new ArrayList<>();

    @Builder.Default
    private Long likeCount = 0L;

    @Builder.Default
    private Long viewCount = 0L;

    @Builder.Default
    private Long commentsCount = 0L;

    @Builder.Default
    private List<CommentCache> comments = new ArrayList<>();
}
