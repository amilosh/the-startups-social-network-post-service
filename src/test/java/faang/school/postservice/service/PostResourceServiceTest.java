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
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostResourceServiceTest {

    @InjectMocks
    private PostResourceService postResourceService;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private ResourceMapper resourceMapper;

    @Mock
    private ImageResizer imageResizer;

    @Test
    void addPostImageSuccessTest() throws Exception {
        ReflectionTestUtils.setField(postResourceService, "maxImageSizeMb", 5);
        ReflectionTestUtils.setField(postResourceService, "maxCountImagesForPost", 10);

        Long postId = 1L;
        byte[] imageData = Files.readAllBytes(Paths.get("src/test/resources/img.jpg"));

        MultipartFile image =
                new MockMultipartFile(
                        "Test",
                        "image.jpg",
                        "image/jpeg",
                        imageData
                );
        Post post = new Post();
        post.setId(postId);
        String key = "s3-key";
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setName("image.jpg");
        resource.setSize(5);
        resource.setType("image/jpeg");
        resource.setPost(post);

        ResourceDto resourceDto = new ResourceDto(key, 1L, LocalDateTime.now(), "Test", "image/jpeg", 1L);

        when(resourceMapper.toDto(any(Resource.class)))
                .thenReturn(resourceDto);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(imageResizer.resizeImage(image, 0, 0)).thenReturn(image);
        when(resourceRepository.countImagesForPostById(postId)).thenReturn(8);
        when(s3Service.uploadFile(eq(image), eq("Post" + postId))).thenReturn(key);

        ResourceDto result = postResourceService.addPostImage(postId, image);

        assertThat(result.key(), equalTo(key));
        verify(resourceRepository).save(any(Resource.class));
        verify(s3Service).uploadFile(image, "Post" + postId);
    }

    @Test
    void addPostImageWhenImageSizeExceedsLimitFailsTest() throws Exception  {
        ReflectionTestUtils.setField(postResourceService, "maxImageSizeMb", 1);

        Long postId = 1L;

        byte[] oversizedImageData = Files.readAllBytes(Paths.get("src/test/resources/img.jpg"));
        MultipartFile oversizedImage = new MockMultipartFile(
                "Test",
                "large-image.jpg",
                "image/jpeg",
                oversizedImageData
        );

        Post post = new Post();
        post.setId(postId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postResourceService.addPostImage(postId, oversizedImage));

        assertThat(exception.getMessage(),equalTo("The image size must not exceed 5MB."));

        verify(resourceRepository, never()).save(any(Resource.class));
        verify(s3Service, never()).uploadFile(any(MultipartFile.class), any(String.class));
    }
    @Test
    void deleteImageByKeySuccessTest() {
        String key = "s3-key";
        Resource resource = new Resource();
        resource.setId(101L);
        resource.setKey(key);

        when(resourceRepository.findByKey(key)).thenReturn(Optional.of(resource));

        long deletedId = postResourceService.deleteImageByKey(key);

        assertThat(deletedId, equalTo(101L));
        verify(resourceRepository).deleteByKey(key);
        verify(s3Service).deleteFile(key);
    }

    @Test
    void deleteImageByKeyWhenImageDoesNotExistFailTest() {
        String key = "nonexistent-key";

        when(resourceRepository.findByKey(key)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postResourceService.deleteImageByKey(key));

        assertThat(exception.getMessage(), equalTo("Image with key '" + key + "' not found"));
    }

    @Test
    void getImageByKeySuccessTest() {
        String key = "s3-key";
        byte[] imageData = "image data".getBytes();
        InputStream inputStream = new ByteArrayInputStream(imageData);

        when(s3Service.downloadFile(key)).thenReturn(inputStream);

        byte[] result = postResourceService.getImageByKey(key);

        assertThat(result, equalTo(imageData));
        verify(s3Service).downloadFile(key);
    }

    @Test
    void getImageByKeyWhenImageDoesNotExistFailTest() {
        String key = "nonexistent-key";

        when(s3Service.downloadFile(key)).thenThrow(new EntityNotFoundException("Image with key '" + key + "' not found"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> postResourceService.getImageByKey(key));

        assertThat(exception.getMessage(), equalTo("Image with key '" + key + "' not found"));
    }

    @Test
    void getAllImagesByPostIdSuccessTest() {
        Long postId = 1L;
        String key1 = "s3-key-1";
        String key2 = "s3-key-2";
        byte[] imageData1 = "image1 data".getBytes();
        byte[] imageData2 = "image2 data".getBytes();

        InputStream inputStream1 = new ByteArrayInputStream(imageData1);
        InputStream inputStream2 = new ByteArrayInputStream(imageData2);

        when(resourceRepository.getAllKeysForPost(postId)).thenReturn(Optional.of(List.of(key1, key2)));
        when(s3Service.downloadFile(key1)).thenReturn(inputStream1);
        when(s3Service.downloadFile(key2)).thenReturn(inputStream2);

        List<byte[]> result = postResourceService.getAllImagesByPostId(postId);

        assertThat(result, hasSize(2));
        assertThat(result.get(0), equalTo(imageData1));
        assertThat(result.get(1), equalTo(imageData2));
    }

    @Test
    void getAllImagesByPostIdWhenPostDoesNotExistFailTest() {
        Long postId = 1L;

        when(resourceRepository.getAllKeysForPost(postId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postResourceService.getAllImagesByPostId(postId));

        assertThat(exception.getMessage(), equalTo("Image for id Post " + postId + " not found"));
    }

    @Test
    void deleteAllPostImagesSuccessTest() {
        Long postId = 1L;
        String key1 = "s3-key-1";
        String key2 = "s3-key-2";
        Resource resource1 = new Resource();
        resource1.setId(101L);
        resource1.setKey(key1);
        Resource resource2 = new Resource();
        resource2.setId(102L);
        resource2.setKey(key2);

        when(resourceRepository.getAllKeysForPost(postId)).thenReturn(Optional.of(List.of(key1, key2)));
        when(resourceRepository.findByKey(key1)).thenReturn(Optional.of(resource1));
        when(resourceRepository.findByKey(key2)).thenReturn(Optional.of(resource2));

        List<Long> result = postResourceService.deleteAllPostImages(postId);

        assertThat(result, Matchers.contains(101L, 102L));
        verify(resourceRepository).deleteByKey(key1);
        verify(resourceRepository).deleteByKey(key2);
        verify(s3Service).deleteFile(key1);
        verify(s3Service).deleteFile(key2);
    }

    @Test
    void deleteAllImageByPostIdWhenPostDoesNotExistFailTest() {
        Long postId = 1L;

        when(resourceRepository.getAllKeysForPost(postId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postResourceService.deleteAllPostImages(postId));

        assertThat(exception.getMessage(), equalTo("Image for id Post " + postId + " not found"));
    }
}
