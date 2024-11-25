package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Month;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumFilterDto {

    @Size(min = 3, max = 128, message = "Title must be between 3 and 128 characters.")
    private String title;

    @Size(min = 10, max = 4096, message = "Description must be between 10 and 4096 characters.")
    private String description;

    private Month month;
}
