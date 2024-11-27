package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceType;
import faang.school.postservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ResourceHandler {
    Resource addResource(MultipartFile file, String folder);

    void deleteResource(String key);

    InputStream getResource(String key);

    ResourceType getType();
}