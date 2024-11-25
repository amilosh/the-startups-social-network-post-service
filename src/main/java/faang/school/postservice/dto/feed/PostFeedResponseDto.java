package faang.school.postservice.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostFeedResponseDto {
    private Long id;
    private String content;
    private Integer likes;
    private Integer views;
    private UserFeedResponseDto author;
    private List<CommentFeedResponseDto> comments;
}
