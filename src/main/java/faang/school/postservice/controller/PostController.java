package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.utilities.UrlUtils;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Log4j2
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST)
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Long> draftPost(@RequestBody @NotNull PostDto postDto) {
        checkPostDtoContainsContent(postDto);
        checkIdUserOrIdProjectNotNull(postDto);
        Long postId = postService.createDraftPost(postDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }

    @PatchMapping(UrlUtils.ID)
    public ResponseEntity<PostDto> publishPost(@PathVariable("id") @Min(1) Long postId) {
        PostDto postDto = postService.publishPost(postId);
        return ResponseEntity.ok().body(postDto);
    }

    @PutMapping(UrlUtils.ID)
    public ResponseEntity<PostDto> updatePost(@PathVariable("id") @Min(1) Long postId, @RequestBody @NotNull PostDto postDto) {
        checkIdUserOrIdProjectNotNull(postDto);
        PostDto postDtoUpdated = postService.updatePost(postId, postDto);
        return ResponseEntity.ok().body(postDtoUpdated);
    }

    @DeleteMapping(UrlUtils.ID)
    public ResponseEntity<Long> deletePost(@PathVariable("id") @Min(1) Long postId) {
        Long postDeletedId = postService.deletePost(postId);
        return ResponseEntity.ok().body(postDeletedId);
    }

    @GetMapping
    public ResponseEntity<PostDto> getPost(@RequestParam @Min(1) Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping(UrlUtils.USER + UrlUtils.DRAFT)
    public ResponseEntity<List<PostDto>> getDraftPostsByUserId(@RequestParam @Min(1) Long idUser) {
        return ResponseEntity.ok(postService.getDraftPostsForUser(idUser));
    }

    @GetMapping(UrlUtils.PROJECT + UrlUtils.DRAFT)
    public ResponseEntity<List<PostDto>> getDraftPostsByProjectId(@RequestParam @Min(1) Long idProject) {
        return ResponseEntity.ok(postService.getDraftPostsForProject(idProject));
    }

    @GetMapping(UrlUtils.USER + UrlUtils.PUBLISHED)
    public ResponseEntity<List<PostDto>> getPublishedPostsByUserId(@RequestParam @Min(1) Long idUser) {
        return ResponseEntity.ok(postService.getPublishedPostsForUser(idUser));
    }

    @GetMapping(UrlUtils.PROJECT + UrlUtils.PUBLISHED)
    public ResponseEntity<List<PostDto>> getPublishedPostsByProjectId(@RequestParam @Min(1) Long idProject) {
        return ResponseEntity.ok(postService.getPublishedPostForProject(idProject));
    }

    private void checkPostDtoContainsContent(PostDto postDto) {
        if (postDto.content() == null) {
            log.error("Field Content is NULL");
            throw new IllegalArgumentException("Field Content is NULL");
        }
        if (postDto.content().isBlank()) {
            log.error("Field Content is blank");
            throw new IllegalArgumentException("Field Content is blank");
        }
    }

    private void checkIdUserOrIdProjectNotNull(PostDto postDto) {
        if (postDto.projectId() == null && postDto.userId() == null) {
            log.error("idProject or idUser are NULL");
            throw new IllegalArgumentException("idProject or idUser are NULL");
        }
        if (postDto.projectId() != null && postDto.userId() != null) {
            log.error("idProject or idUser both has value");
            throw new IllegalArgumentException("idProject or idUser both has value");
        }
    }
}


