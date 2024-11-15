package faang.school.postservice.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class PostDto {

    @PositiveOrZero(message = "ID is required")
    private long id;
}
