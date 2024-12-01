package faang.school.postservice.dto.like;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static faang.school.postservice.utils.LocalDateTimePatterns.DATE_TIME_PATTERN;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponseDto {
    private Long id;
    private Long postId;
    private Long commentId;
    private Long userId;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createdAt;
}
