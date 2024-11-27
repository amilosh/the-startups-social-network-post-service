package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toEntity(PostResponseDto postResponseDto);

    PostResponseDto toDto (Post post);

    Post toEntity(PostRequestDto postRequestDto);

    Post toEntity(PostUpdateDto postUpdateDto);

    List<PostResponseDto> toDtoList(List<Post> posts);
}
