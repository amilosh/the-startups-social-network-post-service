package faang.school.postservice.dto.album;

import lombok.Data;

import java.util.List;

@Data
public class AlbumRequestUpdateDto {

    private Long id;
    private String title;
    private String description;
    private Long authorId;
    private List<Long> postsIds;

}
