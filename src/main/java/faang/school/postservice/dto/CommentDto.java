package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    private Long id;
    @NotBlank
    @Size(max = 4096, message = "The size exceeds 4096 characters")
    private String content;
    @NotNull
    private Long authorId;
    @NotNull
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
