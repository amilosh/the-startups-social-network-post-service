package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.service.ResourceService;
import faang.school.postservice.utilities.UrlUtils;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Log4j2
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.RESOURCE)
public class ResourceController {

    private final ResourceService resourceService;

    @PutMapping(UrlUtils.ID + UrlUtils.ADD)
    public ResponseEntity<ResourceDto> addImage(@PathVariable("id") @Min(1) Long postId, @RequestBody @NotNull MultipartFile image) {
        ResourceDto resourceDto = resourceService.addPostImage(postId, image);
        return ResponseEntity.ok().body(resourceDto);
    }

    @DeleteMapping(UrlUtils.ID)
    public ResponseEntity<Long> deleteImageByKey(@PathVariable("id") @Min(1) Long postId, @RequestParam @NotBlank String key) {
        Long idImage = resourceService.deletePostImageByKey(postId, key);
        return ResponseEntity.ok().body(idImage);
    }

    @GetMapping
    public ResponseEntity<byte[]> getImageByKey(@RequestParam @NotBlank String key) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(resourceService.getImageByKey(key));
    }

    @GetMapping
    public List<ResponseEntity<byte[]>> getAllImagesByPostId(@PathVariable("id") @Min(1) Long postId) {
        List<byte[]> images = resourceService.getAllImagesByPostId(postId);
        return images.stream()
                .map(image -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                        .body(image))
                .toList();
    }

    @DeleteMapping(UrlUtils.ID)
    public ResponseEntity<List<Long>> deleteAllImageByPostId(@PathVariable("id") @Min(1) Long postId) {
        List<Long> idImages = resourceService.deleteAllPostImages(postId);
        return ResponseEntity.ok().body(idImages);
    }
}