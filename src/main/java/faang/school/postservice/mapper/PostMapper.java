package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;

@Mapper
public interface PostMapper {

    PostDto toDto(Post post);

    Post toEntity(PostDto postDto);

}
