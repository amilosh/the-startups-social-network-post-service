package faang.school.postservice.mapper;

import faang.school.postservice.model.dto.UserWithFollowersDto;
import faang.school.postservice.model.dto.redis.cache.RedisUserDto;
import faang.school.postservice.model.entity.UserShortInfo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserWithFollowersMapper {

    RedisUserDto toRedisUserDto(UserWithFollowersDto dto);

    UserShortInfo toUserShortInfo(UserWithFollowersDto dto);
}
