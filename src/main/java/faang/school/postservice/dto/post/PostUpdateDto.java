package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostUpdateDto {
    @NotNull
    @NotBlank
    private Long id;
    @NotNull
    @NotBlank
    private String content;
    private LocalDateTime updatedAt;

}
