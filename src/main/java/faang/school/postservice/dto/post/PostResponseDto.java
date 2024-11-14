package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {

    private Long id;
    private Long authorId;
    private Long projectId;
    private String content;
    private Long countLikes;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

}
