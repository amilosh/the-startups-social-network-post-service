package faang.school.postservice.service;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3ServiceImpl;
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
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final PostRepository postRepository;
    private final S3ServiceImpl s3Service;
    private final ResourceMapper resourceMapper;
    private final ImageResizer imageResizer;

    @Value("${services.s3.max-image-size-mb}")
    private long maxImageSizeMb;
    @Value("${services.s3.max-images-count-for-post}")
    private int maxCountImagesForPost;

    public ResourceDto addPostImage(Long postId, MultipartFile image) {
        Post post = checkPostExist(postId);

        checkAndResizeImage(image);
        checkImagesCountForPost(postId);

        String folder = "Post" + postId;
        Resource resource = s3Service.uploadFile(image, folder);
        resource.setPost(post);
        resourceRepository.save(resource);

        return resourceMapper.toDto(resource);
    }

    @Transactional
    public long deletePostImageByKey(String key) {
        Resource resource = resourceRepository.findByKey(key);
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
        List<String> listKeys = resourceRepository.getAllKeysForPost(postId);
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
        List<String> allKeys = resourceRepository.getAllKeysForPost(postId);
        List<Long> listIdResources = new ArrayList<>();
        if (!allKeys.isEmpty()) {
            listIdResources = allKeys.stream().map(key -> resourceRepository.findByKey(key).getId()).toList();
            allKeys.forEach(resourceRepository::deleteByKey);
            allKeys.forEach(s3Service::deleteFile);
        }
        return listIdResources;
    }

    private void checkAndResizeImage(MultipartFile image) {
        try {
            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
            if (bufferedImage == null) {
                log.error("The file is not a valid image.");
                throw new IllegalArgumentException("The uploaded file is not a valid image.");
            }
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            if (width > height) {
                if (width > 1080 || height > 566) {
                    imageResizer.resizeImage(image, 1080, 566);
                }
            } else if (width == height) {
                if (width > 1080) {
                    imageResizer.resizeImage(image, 1080, 1080);
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
    }


    private Post checkPostExist(Long postId) {
        return postRepository.findById(postId).
                orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));
    }

    private void checkImagesCountForPost(Long postId) {
        if (resourceRepository.countImagesForPostById(postId) > maxCountImagesForPost) {
            log.error("One post can has {} images, not more  postId={}", maxCountImagesForPost, postId);
            throw new IllegalArgumentException(String.format("One post can has %d images, not more  postId=%d", maxCountImagesForPost, postId));
        }
    }

}