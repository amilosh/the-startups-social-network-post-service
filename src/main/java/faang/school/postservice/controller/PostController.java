package faang.school.postservice.controller;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.utilities.UrlUtils;
import feign.FeignException;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.POST)
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;


    @PostMapping
    public ResponseEntity<Void> draftPost(@RequestBody PostDto postDto) {
        checkPostDtoContainsContent(postDto);
        checkIdUserAndIdProjectNotNull(postDto);
        postService.createDraftPost(postDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping(UrlUtils.ID + UrlUtils.PUBLISH)
    public ResponseEntity<Void> publishPost(@PathVariable("id") @Min(1) Long postId) {
        checkPostExistsById(postId);
        postService.publishPost(postId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(UrlUtils.ID + UrlUtils.UPDATE)
    public ResponseEntity<Void> updatePost(@PathVariable("id") @Min(1) Long postId, @RequestBody String content) {
        checkPostExistsById(postId);
        postService.updatePost(postId, content);
        return ResponseEntity.ok().build();
    }

    @PutMapping(UrlUtils.ID + UrlUtils.DELETE)
    public ResponseEntity<Void> deletePost(@PathVariable("id") @Min(1) Long postId) {
        checkPostExistsById(postId);
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<PostDto> getPost(@RequestParam(required = false) Long postId) {
        checkPostExistsById(postId);
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping(UrlUtils.USER + UrlUtils.DRAFT)
    public ResponseEntity<List<PostDto>> getDraftPostsByUserId(@RequestParam(required = false) Long idUser) {
        checkByIdUserExist(idUser);
        return ResponseEntity.ok(postService.getDraftPostsForUser(idUser));
    }

    @GetMapping(UrlUtils.PROJECT + UrlUtils.DRAFT)
    public ResponseEntity<List<PostDto>> getDraftPostsByProjectId(@RequestParam(required = false) Long idProject) {
        checkByIdProjectExist(idProject);
        return ResponseEntity.ok(postService.getDraftPostsForProject(idProject));
    }

    @GetMapping(UrlUtils.USER + UrlUtils.PUBLISHED)
    public ResponseEntity<List<PostDto>> getPublishedPostsByUserId(@RequestParam(required = false) Long idUser) {
        checkByIdUserExist(idUser);
        return ResponseEntity.ok(postService.getPublishedPostsForUser(idUser));
    }

    @GetMapping(UrlUtils.PROJECT + UrlUtils.PUBLISHED)
    public ResponseEntity<List<PostDto>> getPublishedPostsByProjectId(@RequestParam(required = false) Long idProject) {
        checkByIdProjectExist(idProject);
        return ResponseEntity.ok(postService.getPublishedPostForProject(idProject));
    }

    private void checkPostDtoContainsContent(PostDto postDto) {
        if (postDto.content().isBlank()) {
            log.error("Field Content is blank");
            throw new IllegalArgumentException("Field Content is blank");
        }
    }

    private void checkPostExistsById(Long id) {
        if (postRepository.findById(id).isEmpty()) {
            log.error("Post with Id: " + id + "not found");
            throw new IllegalArgumentException("Post with Id: " + id + "not found");
        }
    }

    private void checkIdUserAndIdProjectNotNull(PostDto postDto) {
        if (postDto.idProject() == null && postDto.idUser() == null) {
            log.error("idProject and idUser are NULL");
            throw new IllegalArgumentException("idProject and idUser equals");
        }
    }

    private void checkByIdUserExist(Long idUser) {
        try {
            userServiceClient.getUser(idUser);
        } catch (FeignException e) {
            log.error("User id:" + idUser + "Error" + e.getMessage());
            throw new IllegalArgumentException("User id:" + idUser + "Error" + e.getMessage());
        }
    }

    private void checkByIdProjectExist(Long idProject) {
        try {
            projectServiceClient.getProject(idProject);
        } catch (FeignException e) {
            log.error("Project id:" + idProject + "Error" + e.getMessage());
            throw new IllegalArgumentException("Project id:" + idProject + "Error" + e.getMessage());
        }
    }
}


