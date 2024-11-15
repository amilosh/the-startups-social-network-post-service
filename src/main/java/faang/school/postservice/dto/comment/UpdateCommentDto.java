package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentDto {

    @NotNull(message = "Comment id cannot be empty")
    @Positive(message = "Comment id should be a positive number")
    private Long id;

    @NotNull(message = "Author id cannot be empty")
    @Positive(message = "Author is should be a positive number")
    private Long authorId;

    @NotBlank(message = "Comment cannot be empty")
    private String content;
}
