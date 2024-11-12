package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostDto {

    private Long id;

    @NotBlank(message = "Content must be not blank")
    @Size(max = 1000, message = "Content must be shorter than 1000 characters")
    private String content;

    private Long authorId;

    private Long projectId;

    private boolean published;

    private LocalDateTime publishedAt;

    private LocalDateTime scheduledAt;

    private boolean deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<Long> likesIds;
}