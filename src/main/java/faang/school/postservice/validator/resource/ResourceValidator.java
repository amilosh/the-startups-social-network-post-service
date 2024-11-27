package faang.school.postservice.validator.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.dto.resource.ResourceType;
import faang.school.postservice.exception.ResourceLimitExceededException;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResourceValidator {

    private static final long MAX_IMAGES_PER_POST = 10;

    public void validateImagesCountPerPost(List<ResourceDto> resources, Post post) {
        long currentImageCount = post.getResources().stream()
                .filter(resource -> ResourceType.IMAGE.name().equalsIgnoreCase(resource.getType()))
                .count();
        long newImageCount = resources.stream()
                .filter(dto -> dto.getType() == ResourceType.IMAGE)
                .count();
        if (currentImageCount + newImageCount > MAX_IMAGES_PER_POST) {
            log.error("ImagesCountPerPost Validation Failed for post {}", post.getId());
            throw new ResourceLimitExceededException("Cannot upload more than" + MAX_IMAGES_PER_POST + " images for a single post");
        }
    }
}