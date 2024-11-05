package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.dto.like.RedisPostLikeEvent;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "comment.id", target = "commentId")
    LikeResponseDto toDto(Like entity);

    @Mapping(source = "like.userId", target = "likeAuthorId")
    @Mapping(source = "like.post.id", target = "postId")
    @Mapping(source = "like.post.authorId", target = "postAuthorId")
    RedisPostLikeEvent toRedisPostLikeEvent(Like like);

}
