package faang.school.postservice.dto.post;

import faang.school.postservice.validator.resource.ValidResourceFileSize;
import faang.school.postservice.validator.resource.ValidResourceFileType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateDto {
    private String content;
    @Size(max = 10, message = "You can only have 10 images in your post")
    @ValidResourceFileSize(resourceType = "image", maxSizeInBytes = 5 * 1024 * 1024)
    @ValidResourceFileType(resourceType = "image")
    private List<MultipartFile> images;
    @Size(max = 10, message = "You can only have 5 audio in your post")
    @ValidResourceFileSize(resourceType = "audio", maxSizeInBytes = 10 * 1024 * 1024)
    @ValidResourceFileType(resourceType = "audio")
    private List<MultipartFile> audio;
    private List<Long> imageFilesIdsToDelete;
    private List<Long> audioFilesIdsToDelete;
}