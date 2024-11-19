package faang.school.postservice.service.post.filter;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PostFilter {
    private final PostMapper postMapper;

    public List<PostDto> filterPostByTimeToDTo(List<Post> posts, boolean published){
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished() == published)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }
}

