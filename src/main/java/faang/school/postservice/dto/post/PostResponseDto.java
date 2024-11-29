package faang.school.postservice.dto.post;

import faang.school.postservice.dto.resource.ResourceResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private Long id;
    private Long authorId;
    private Long projectId;
    private boolean published;
    private boolean deleted;
    private String content;
    private List<Long> likeIds;
    private List<Long> commentIds;
    private LocalDateTime createdAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private List<ResourceResponseDto> images;
    private List<ResourceResponseDto> audio;
}