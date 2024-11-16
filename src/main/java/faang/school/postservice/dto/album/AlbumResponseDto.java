package faang.school.postservice.dto.album;

import java.time.LocalDateTime;
import java.util.List;

public class AlbumResponseDto {

    private Long id;
    private String title;
    private String description;
    private Long authorId;
    List<Long> postsIds;
    LocalDateTime createdAt;

}
