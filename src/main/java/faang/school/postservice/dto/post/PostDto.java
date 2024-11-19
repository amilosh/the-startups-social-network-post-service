package faang.school.postservice.dto.post;

import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PostDto {
    private Long id;
    @NotNull
    @Size(min = 1, max = 4096)
    @NotBlank
    private String content;
    @Min(0)
    private Long authorId;
    @Min(0)
    private Long projectId;
    private List<Long> likesIds;
    private List<Long> commentsIds;
    private List<Long> albumsIds;

}
