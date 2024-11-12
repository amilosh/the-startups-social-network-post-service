package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDto {

    @NotBlank(message = "Comment cannot be empty")
    @Size(max = 4096, message = "Max size of comment is 4095 symbols")
    private String content;

    @NotNull(message = "Author id cannot be empty")
    @Positive(message = "Author id should be positive number")
    Long authorId;
}

