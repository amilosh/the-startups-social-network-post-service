package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(source = "likes", target = "likesIds", qualifiedByName = "mapLikesToIds")
    PostDto toDto(Post post);

    Post toEntity(PostDto postDto);

    List<PostDto> toDto(List<Post> posts);

    void update(PostDto postDto, @MappingTarget Post post);

    @Named("mapLikesToIds")
    default List<Long> mapLikesToIds(List<Like> likes) {
        return likes == null ? List.of() : likes.stream()
                .map(Like::getId)
                .collect(Collectors.toList());
    }
}
