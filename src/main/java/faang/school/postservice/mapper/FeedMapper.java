package faang.school.postservice.mapper;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.model.Feed;
import faang.school.postservice.model.redis.FeedCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeedMapper {

    @Mapping(source = "post.id", target = "postId")
    FeedDto feedToFeedDto(Feed feed);

    List<FeedDto> feedToFeedDto(List<Feed> feed);

}
