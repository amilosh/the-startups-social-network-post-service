package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceType;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageResourceHandler implements ResourceHandler {
    private final S3Service s3Service;
    private final static ResourceType type = ResourceType.IMAGE;

    @Override
    public Resource addResource(MultipartFile file, String folder) {
        log.info("File with the name {} processed by Image handler", file.getOriginalFilename());
        return s3Service.uploadFile(file, folder);
    }

    @Override
    public void deleteResource(String key) {
        log.info("Deleting image with key {} with the image handler", key);
        s3Service.deleteFile(key);
    }

    @Override
    public InputStream getResource(String key) {
        log.info("Retrieving image with key {} with the image handler", key);
        return s3Service.downloadFile(key);
    }

    @Override
    public ResourceType getType() {
        return type;
    }
}