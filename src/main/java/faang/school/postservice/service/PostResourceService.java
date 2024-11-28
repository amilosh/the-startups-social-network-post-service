package faang.school.postservice.service;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.utilities.ImageResizer;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class PostResourceService {

    private final ResourceRepository resourceRepository;
    private final PostRepository postRepository;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;
    private final ImageResizer imageResizer;

    @Value("${services.s3.max-image-size-mb}")
    private long maxImageSizeMb;
    @Value("${services.s3.max-images-count-for-post}")
    private int maxCountImagesForPost;
    @Value("${services.s3.max-width-horizontal-image}")
    private int maxWidthHorizontalImage;
    @Value("${services.s3.max-height-horizontal-image}")
    private int maxHeightHorizontalImage;
    @Value("${services.s3.max-side-square-image}")
    private int maxSideSquareImage;

    public ResourceDto addPostImage(Long postId, MultipartFile image) {
        Post post = getPostOrThrow(postId);

        image = resizeOrThrow(image);
        checkImagesCountForPost(postId);

        String folder = "Post" + postId;
        String key = s3Service.uploadFile(image, folder);
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setName(image.getOriginalFilename());
        resource.setSize(image.getSize());
        resource.setType(image.getContentType());
        resource.setCreatedAt(LocalDateTime.now());
        resource.setPost(post);
        resourceRepository.save(resource);

        return resourceMapper.toDto(resource);
    }

    @Transactional
    public long deleteImageByKey(String key) {
        Resource resource = getResourceOrThrow(key);

        resourceRepository.deleteByKey(key);

        s3Service.deleteFile(key);
        return resource.getId();
    }

    public byte[] getImageByKey(String key) {
        try (InputStream inputStream = s3Service.downloadFile(key)) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("File processing error", e);
            throw new RuntimeException("File processing error", e);
        }
    }

    public List<byte[]> getAllImagesByPostId(Long postId) {
        List<String> listKeys = resourceRepository.getAllKeysForPost(postId).orElseThrow(()
                -> {
            log.error("Image for id Post '{}' not found", postId);
            return new EntityNotFoundException("Image for id Post " + postId + " not found");
        });
        List<byte[]> bytesList = new ArrayList<>();
        listKeys.forEach(key -> {
            try (InputStream inputStream = s3Service.downloadFile(key)) {
                bytesList.add(inputStream.readAllBytes());
            } catch (IOException e) {
                log.error("File processing error", e);
                throw new RuntimeException("File processing error", e);
            }
        });
        return bytesList;
    }

    @Transactional
    public List<Long> deleteAllPostImages(Long postId) {
        List<String> allKeys = resourceRepository.getAllKeysForPost(postId).orElseThrow(()
                -> {
            log.error("Image for id Post '{}' not found", postId);
            return new EntityNotFoundException("Image for id Post " + postId + " not found");
        });
        List<Long> listIdResources = new ArrayList<>();
        if (!allKeys.isEmpty()) {
            listIdResources = allKeys.stream().map(key -> getResourceOrThrow(key).getId()).toList();
            allKeys.forEach(resourceRepository::deleteByKey);
            allKeys.forEach(s3Service::deleteFile);
        }
        return listIdResources;
    }

    private MultipartFile resizeOrThrow(MultipartFile image) {
        MultipartFile resultImage = image;
        try {
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
            if (bufferedImage == null) {
                log.error("The file is not a valid image.");
                throw new IllegalArgumentException("The uploaded file is not a valid image.");
            }
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            if (width > height) {
                if (width > maxWidthHorizontalImage || height > maxHeightHorizontalImage) {
                    resultImage = imageResizer.resizeImage(image, maxWidthHorizontalImage, maxHeightHorizontalImage);
                }
            } else if (width == height) {
                if (width > maxSideSquareImage) {
                    resultImage = imageResizer.resizeImage(image, maxSideSquareImage, maxSideSquareImage);
                }
            } else {
                log.error("Wrong size of image " + width + "x" + height);
                throw new IllegalArgumentException("Wrong size of image " + width + "x" + height);
            }
            log.info("Image dimensions are valid: width={}, height={}", width, height);

        } catch (IOException e) {
            log.error("Failed to read the uploaded image.", e);
            throw new IllegalArgumentException("Failed to process the uploaded image.");
        }

        if (image.getSize() > maxImageSizeMb * 1024 * 1024) {
            log.error("The image size must not exceed 5MB");
            throw new IllegalArgumentException("The image size must not exceed 5MB.");
        }
        return resultImage;
    }

    private Resource getResourceOrThrow(String key) {
        return resourceRepository.findByKey(key).orElseThrow(()
                -> {
            log.error("Image with key '{}' not found", key);
            return new EntityNotFoundException("Image with key '" + key + "' not found");
        });
    }

    private Post getPostOrThrow(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Error: Post with id {} was deleted", postId);
                    return new EntityNotFoundException("Post not found with ID: " + postId);
                });
        if (post.isDeleted()) {
            log.error("Error: Post with id {}was deleted", postId);
            throw new IllegalArgumentException("Error: Post with id " + postId + "was deleted");
        }
        return post;
    }

    private void checkImagesCountForPost(Long postId) {
        if (resourceRepository.countImagesForPostById(postId) > maxCountImagesForPost) {
            log.error("One post can has {} images, not more  postId={}", maxCountImagesForPost, postId);
            throw new IllegalArgumentException(String.format("One post can has %d images, not more  postId=%d",
                    maxCountImagesForPost, postId));
        }
    }
}