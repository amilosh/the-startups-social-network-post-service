package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDto {
    private Long id;

    @NotBlank(message = "content must be not blank")
    @NotNull(message = "content must be not null")
    @Size(max = 1000, message = "content must be shorter than 1000 characters")
    private String content;

    private Long authorId;
    private Long ProjectId;
    private List<Long> likeIds;
    private List<Long> commentIds;
    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
}