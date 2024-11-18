package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.postservice.model.VerificationPostStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import static faang.school.postservice.utils.LocalDateTimePatterns.DATE_TIME_PATTERN;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private Boolean published;
    private Boolean deleted;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime publishedAt;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime scheduledAt;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime updatedAt;

    private VerificationPostStatus verificationStatus;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime verifiedDate;

    private List<Long> resourceIds;
    private Integer likes;
}
