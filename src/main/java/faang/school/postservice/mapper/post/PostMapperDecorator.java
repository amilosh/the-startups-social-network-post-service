package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.resource.ResourceResponseDto;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class PostMapperDecorator implements PostMapper {

    @Autowired
    private PostMapper delegate;

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private S3Service s3Service;

    @Override
    public PostResponseDto toDto(Post post) {
        PostResponseDto dto = delegate.toDto(post);

        List<ResourceResponseDto> images = post.getResources().stream()
                .filter(resource -> "image".equalsIgnoreCase(resource.getType()))
                .map(this::mapResourceToDto)
                .toList();
        dto.setImages(images);

        List<ResourceResponseDto> audio = post.getResources().stream()
                .filter(resource -> "audio".equalsIgnoreCase(resource.getType()))
                .map(this::mapResourceToDto)
                .toList();
        dto.setAudio(audio);

        return dto;
    }

    private ResourceResponseDto mapResourceToDto(Resource resource) {
        ResourceResponseDto dto = resourceMapper.toDto(resource);
        dto.setDownloadUrl(s3Service.generatePresignedUrl(resource.getKey()));
        return dto;
    }
}