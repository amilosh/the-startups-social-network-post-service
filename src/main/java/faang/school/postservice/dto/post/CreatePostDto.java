package faang.school.postservice.dto.post;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Post content cannot be blank")
    @Size(max = 4096, message = "Post content must be between 1 and 4096 characters")
    private String content;

    @Positive(message = "Author id must be positive")
    private Long authorId;

    @Positive(message = "Project id must be positive")
    private Long projectId;

    @FutureOrPresent(message = "Date must be present day or future")
    private LocalDateTime scheduledAt;
}
