package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceType;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceHandlerFactory handlerFactory;
    private final PostRepository postRepository;

    @Transactional
    public Resource add(Long postId, MultipartFile file, ResourceType type) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be null or empty");
        }
        Post post = postRepository.getPostById(postId);
        if (post == null) {
            throw new EntityNotFoundException("Post with id " + postId + " not found");
        }
        String folder = "Post with id: " + postId;

        ResourceHandler handler = handlerFactory.getHandler(type);
        Resource resource = handler.addResource(file, folder);
        log.info("File {} added to the folder {} in the cloud", file.getOriginalFilename(), folder);
        resource.setPost(post);

        post.getResources().add(resource);

        resourceRepository.save(resource);
        return resource;
    }

    @Transactional
    public void delete(Long resourceId, ResourceType type) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        ResourceHandler handler = handlerFactory.getHandler(type);
        handler.deleteResource(resource.getKey());
        log.info("Delete resource with id: {} from cloud", resourceId);
        resourceRepository.deleteById(resourceId);
        log.info("Deleted resource with id: {} from repository.", resourceId);
    }

    @Transactional
    public InputStream get(Long resourceId, ResourceType type) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        log.info("Found resource with id: {} from resourceRepository", resourceId);
        ResourceHandler handler = handlerFactory.getHandler(type);
        log.info("Assigned a handler for the type {} to get resource with id: {} from cloud", type.getClass(), resourceId);
        return handler.getResource(resource.getKey());
    }
}