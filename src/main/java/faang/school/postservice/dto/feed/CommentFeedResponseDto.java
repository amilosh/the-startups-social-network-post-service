package faang.school.postservice.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentFeedResponseDto {
    private Long id;
    private String content;
    private Integer likes;
    private Long authorId;
}
