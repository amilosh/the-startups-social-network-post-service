package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.AcceptanceLikeDto;
import faang.school.postservice.dto.like.ReturnLikeDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    Like toLike(AcceptanceLikeDto acceptanceLikeDto);

    @Mapping(source = "comment.id", target = "commentId")
    @Mapping(source = "post.id", target = "postId")
    ReturnLikeDto toReturnLikeDto(Like like);

}
