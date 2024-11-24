package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostFeedEventDto {
    private Long id;
    private List<Long> subscribersIds;
    private LocalDateTime publishedAt;
}