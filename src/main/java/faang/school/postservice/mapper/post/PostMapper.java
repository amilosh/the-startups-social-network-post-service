package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.ReturnPostDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(source = "likes", target = "countLikes", qualifiedByName = "toCountIds")
    ReturnPostDto toDto(Post post);

    Post toEntity(PostDto postDto);

    @Named("toCountIds")
    default Long toInternshipIds(List<Like> likes) {
        return (long) likes.size();
    }
}
