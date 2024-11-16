package faang.school.postservice.dto.album;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AlbumFilterDto {

    private String titlePattern;
    private String descriptionPattern;
    private List<Long> posts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
