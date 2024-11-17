package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostDto(
        Long id,
        @NotBlank(message = "Content cannot be empty")
        @Size(max = 4096, message = "The content must not exceed 4096 characters")
        String content,
        Long authorId,
        Long projectId,
        LocalDateTime publishedAt,
        LocalDateTime scheduledAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean published,
        Boolean deleted
) {
}
