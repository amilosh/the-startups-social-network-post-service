package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
   private final PostService postService;

   @PostMapping("")
    public PostDto createPost(@RequestBody PostDto postDto){
       return postService.create(postDto);
   }

   @PutMapping("{id}/publish")
    public PostDto publishPost(@PathVariable Long id){
       return postService.publishPost(id);
   }

   @PutMapping("/")
    public PostDto updatePost(@RequestBody PostDto postDto){
       return postService.updatePost(postDto);
   }

   @DeleteMapping("/{id]")
    public void deletePost(@PathVariable Long id){
       postService.deletePost(id);
   }

   @GetMapping("/{id}")
    public PostDto getPostById(@PathVariable Long id){
       return postService.getPostById(id);
   }

   @GetMapping("/author/{authorId}/draftsNon")
    public List<PostDto> getAllNonPublishedByAuthorId(@PathVariable Long id){
       return postService.getAllNonPublishedByAuthorId(id);
   }

    @GetMapping("/project/{projectId}/draftsNon")
    public List<PostDto> getAllNonPublishPostByProjectId(@PathVariable Long id){
        return postService.getAllNonPublishPostByProjectId(id);
    }

    @GetMapping("/author/{authorId}/drafts")
    public List<PostDto> getAllPublishedByAuthorId(@PathVariable Long id){
        return postService.getAllNonPublishedByAuthorId(id);
    }

    @GetMapping("/project/{projectId}/drafts")
    public List<PostDto> getAllPublishPostByProjectId(@PathVariable Long id){
        return postService.getAllNonPublishPostByProjectId(id);
    }



}
