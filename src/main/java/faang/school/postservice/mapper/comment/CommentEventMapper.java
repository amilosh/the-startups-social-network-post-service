package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.mapper.DateTimeMapper;
import faang.school.postservice.protobuf.generate.CommentEventProto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;


/**
 * @Todo: необходимо в дальнейшем пересмотреть логику перевода времени, потому что мы используем здесь постоянный часовой пояс
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentEventMapper extends DateTimeMapper {
    CommentEventProto.CommentEvent toProto(CommentEvent commentEvent);

    CommentEvent toEvent(CommentEventProto.CommentEvent proto);
}
