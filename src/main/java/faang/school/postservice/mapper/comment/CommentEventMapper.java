package faang.school.postservice.mapper.comment;

import com.google.protobuf.Timestamp;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.mapper.DateTimeMapper;
import faang.school.postservice.protobuf.generate.CommentEventProto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


/**
 * @Todo: необходимо в дальнейшем пересмотреть логику перевода времени, потому что мы используем здесь постоянный часовой пояс
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentEventMapper extends DateTimeMapper {
    CommentEventProto.CommentEvent toProto(CommentEvent commentEvent);

    CommentEvent toEvent(CommentEventProto.CommentEvent proto);
}
