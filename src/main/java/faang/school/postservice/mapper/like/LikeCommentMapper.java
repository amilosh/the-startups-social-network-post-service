package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface LikeCommentMapper {

//    @Mapping(source = "like.id", target = "id")
//    @Mapping(source = "like.userId", target = "userId")
//    @Mapping(source = "post.id", target = "postId")
//    @Mapping(source = "like.comment.id", target = "commentId")
//    LikeCommentDto toDto(Like like, Post post);

    Like toEntity(LikeCommentDto likeCommentDto);

}
