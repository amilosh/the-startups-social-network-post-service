package faang.school.postservice.dto.album;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumResponseDto {

    private Long id;
    private String title;
    private String description;
    private Long authorId;
    List<Long> postsIds;
    LocalDateTime createdAt;

}
