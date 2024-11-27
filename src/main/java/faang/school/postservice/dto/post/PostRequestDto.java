package faang.school.postservice.dto.post;

import faang.school.postservice.dto.resource.ResourceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDto {
    private Long id;
    private Long authorId;
    private Long projectId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private List<ResourceDto> resources;
}