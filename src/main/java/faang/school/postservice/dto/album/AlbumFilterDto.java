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
public class AlbumFilterDto {

    private String titlePattern;
    private String descriptionPattern;
    private List<Long> posts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
