package faang.school.postservice.redis.mapper;

import faang.school.postservice.model.dto.UserDto;
import faang.school.postservice.redis.model.entity.AuthorCache;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorCacheMapper {

    AuthorCache toAuthorCache(UserDto userDto);
}