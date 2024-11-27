package faang.school.postservice.redis.model.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.TreeSet;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostRedisDto {
    private Long id;
    private String content;
    private AuthorRedisDto author;
    private int numberOfLikes;
    private int numberOfViews;
    private TreeSet<CommentRedisDto> comments;
    private LocalDateTime publishedAt;
}
