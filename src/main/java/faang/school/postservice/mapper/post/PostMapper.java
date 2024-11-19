package faang.school.postservice.mapper.post;

import faang.school.postservice.model.dto.post.PostDto;
import faang.school.postservice.model.entity.Like;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.redis.PostRedis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    PostDto toDto(Post post);

    Post toEntity(PostDto postDto);

    List<PostDto> toDto(List<Post> posts);

    @Mapping(source = "likes", target = "likes", qualifiedByName = "likeCounter")
    PostRedis toRedis(Post publishedPost);

    @Named("likeCounter")
    default Integer likesCounter(List<Like> likes){
        return likes.size();
    }
}
