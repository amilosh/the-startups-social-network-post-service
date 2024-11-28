package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequestDto {


    @NotNull
    @NotBlank
    private Long authorId;
    @NotNull
    @NotBlank
    private String content;
    @NotNull
    @NotBlank
    private Long projectId;



}
