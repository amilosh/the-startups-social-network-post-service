package faang.school.postservice.dto.album;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumRequestDto {

    private Long id;
    private String title;
    private String description;
    private Long authorId;

}
