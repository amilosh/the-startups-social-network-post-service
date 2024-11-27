package faang.school.postservice.dto.resource;

import faang.school.postservice.validator.resource.ValidResourceFileSize;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@ValidResourceFileSize
public class ResourceDto {

    @NotNull
    private MultipartFile file;
    @NotNull
    private String name;
    @NotNull
    private ResourceType type;
    private Long postId;
}