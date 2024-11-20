package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostDto(
        @NotBlank(message = "Content should not be blank")
        @Size(max = 255, message = "Content must not exceed 255 characters")
        String content,
        Long userId,
        Long projectId) {
}
