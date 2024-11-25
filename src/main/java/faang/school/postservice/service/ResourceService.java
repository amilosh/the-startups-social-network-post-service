package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.exeption.ResourceNotFoundException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.validator.FileValidator;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final PostService postService;
    private final UserContext userContext;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final PostValidator postValidator;
    private final FileValidator fileValidator;
    private final ResourceMapper resourceMapper;

    public List<ResourceDto> uploadFiles(Long postId, List<MultipartFile> files) {
        Post post = postService.findPostById(postId);

        postValidator.validateAuthorUpdatesPost(post, userContext.getUserId());
        fileValidator.validateNumberOfFiles(files, 10);
        files.forEach(fileValidator::validateFile);

        String folder = "post_" + postId;
        List<Resource> resources = uploadFiles(files, folder);

        resources.forEach(resource -> resource.setPost(post));

        resourceRepository.saveAll(resources);
        return resourceMapper.toResourceDto(resources);
    }

    public ResourceDto updateFiles(Long resourceId, MultipartFile file) {
        Resource resource = findResourceById(resourceId);
        fileValidator.validateFile(file);
        s3Service.deleteResource(resource.getKey());

        String folder = "post_" + resource.getPost().getId();
        Resource updatedResource = s3Service.uploadFile(file, folder);

        updateResourceFields(resource, updatedResource);

        resourceRepository.save(resource);

        return resourceMapper.toResourceDto(resource);
    }

    public void deleteFiles(Long resourceId) {
        Resource resource = findResourceById(resourceId);
        s3Service.deleteResource(resource.getKey());
        resourceRepository.delete(resource);
    }

    public Resource findResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> {
                    log.warn("Resource with ID {} not found", resourceId);
                    return new ResourceNotFoundException("Resource", "id", resourceId);
                });
    }

    private void updateResourceFields(Resource resource, Resource updatedResource) {
        resource.setKey(updatedResource.getKey());
        resource.setName(updatedResource.getName());
        resource.setSize(updatedResource.getSize());
        resource.setType(updatedResource.getType());
    }

    private List<Resource> uploadFiles(List<MultipartFile> files, String folder) {
        return files.stream()
                .map(file -> s3Service.uploadFile(file, folder))
                .toList();
    }
}


