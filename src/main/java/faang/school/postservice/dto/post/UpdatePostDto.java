package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostDto {
    @NotNull(message = "Post content cannot be null")
    @NotBlank(message = "Post content cannot be blank")
    @Size(min = 1, max = 4096, message = "Post content must be between 1 and 4096 characters")
    private String content;
}
