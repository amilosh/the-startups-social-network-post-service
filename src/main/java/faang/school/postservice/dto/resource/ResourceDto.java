package faang.school.postservice.dto.resource;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ResourceDto {
    private MultipartFile file;
    private String name;
    private ResourceType type;
    private Long postId;
}