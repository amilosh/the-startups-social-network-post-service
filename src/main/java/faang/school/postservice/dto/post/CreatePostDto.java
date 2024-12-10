package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDto {
    @NotBlank(message = "Content can't be blank or empty")
    @Length(max = 4096, message = "Maximum number of characters 4096 symbols")
    private String content;

    private Long authorId;

    private Long projectId;

    private LocalDateTime scheduledAt;
}
