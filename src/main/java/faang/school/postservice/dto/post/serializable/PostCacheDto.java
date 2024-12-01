package faang.school.postservice.dto.post.serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static faang.school.postservice.utils.LocalDateTimePatterns.DATE_TIME_PATTERN;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCacheDto implements PostViewEventParticipant {
    private Long id;
    private String content;
    private Long authorId;
    private UserDto authorDto;

    @Builder.Default
    private Long views = 0L;

    private Long likesCount;
    private Long commentsCount;
    private Set<Long> albumIds;
    private Set<Long> resourceIds;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime publishedAt;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;

    private List<String> hashTags;
    private List<CommentCacheDto> comments;
}
