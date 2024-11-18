package faang.school.postservice.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collection;

import static faang.school.postservice.utils.LocalDateTimePatterns.DATE_TIME_PATTERN;

@Data
public class CommentResponseDto {
    private Long id;
    private String content;
    private Long authorId;
    private Collection<Long> likes;
    private Long postId;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;
}
