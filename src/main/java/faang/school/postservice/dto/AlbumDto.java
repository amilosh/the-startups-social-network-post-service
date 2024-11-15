package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumDto {

    @PositiveOrZero
    private long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 128, message = "Title must be between 3 and 128 characters.")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 4096, message = "Description must be between 10 and 4096 characters.")
    private String description;

    @PositiveOrZero(message = "ID is required")
    private long authorId;

    private List<@PositiveOrZero Long> postsId;
}
