package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDto {
    @NotNull(message = "Post content cannot be null")
    @NotBlank(message = "Post content cannot be blank")
    @Size(min = 1, max = 4096, message = "Post content must be between 1 and 4096 characters")
    private String content;

    @Positive(message = "Author id must be positive")
    private Long authorId;

    @Positive(message = "Project id must be positive")
    private Long projectId;

    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}
