package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDtoInput;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentInputMapper {

    @Mapping(source = "postId", target = "post.id")
    Comment toEntity(CommentDtoInput commentDto);
}