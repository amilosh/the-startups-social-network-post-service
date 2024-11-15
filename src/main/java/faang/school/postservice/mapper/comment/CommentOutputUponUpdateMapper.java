package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDtoOutputUponUpdate;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentOutputUponUpdateMapper {
    @Mapping(source = "post.id", target = "postId")
    CommentDtoOutputUponUpdate toDto(Comment comment);
}