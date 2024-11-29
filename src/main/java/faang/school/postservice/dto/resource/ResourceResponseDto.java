package faang.school.postservice.dto.resource;

import lombok.Data;

@Data
public class ResourceResponseDto {
    private Long id;
    private String type;
    private Long postId;
    private String downloadUrl;
}