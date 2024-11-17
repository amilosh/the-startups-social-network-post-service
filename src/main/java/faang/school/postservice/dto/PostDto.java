package faang.school.postservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;
    @NotNull
    @Size(min = 1, max = 4096, message = "Content must not exceed 4096 characters")
    private String content;
    private Long authorId;
    private Long projectId;
    private List<CommentDto> comments;
    private Long adId;
    private List<Long> resourcesIds;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;
}