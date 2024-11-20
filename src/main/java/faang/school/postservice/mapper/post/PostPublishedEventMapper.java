package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.protobuf.generate.PostPublishedEventProto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostPublishedEventMapper {
    PostPublishedEventProto.PostPublishedEvent toProto(PostPublishedEvent event);

    PostPublishedEvent toEvent(PostPublishedEventProto.PostPublishedEvent postPublishedEvent);
}
