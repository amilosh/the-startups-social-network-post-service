package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.CommentFeedResponseDto;
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
    private Long authorId;
    private Integer likes;
    List<CommentFeedResponseDto> comments;
}
