package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdatePostDto(
        @NotNull(message = "Id cannot be null")
        Long id,
        @NotNull(message = "Content cannot be null")
        String content
) {
}
