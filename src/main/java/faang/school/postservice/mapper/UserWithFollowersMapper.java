package faang.school.postservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.dto.UserWithFollowersDto;
import faang.school.postservice.model.dto.redis.cache.RedisUserDto;
import faang.school.postservice.model.entity.UserShortInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserWithFollowersMapper {

    RedisUserDto toRedisUserDto(UserWithFollowersDto dto);

    @Mapping(target = "followerIds", expression = "java(serializeFollowerIds(dto.getFollowerIds()))")
    UserShortInfo toUserShortInfo(UserWithFollowersDto dto);

    default String serializeFollowerIds(List<Long> followerIds) {
        if (followerIds == null || followerIds.isEmpty()) {
            return "[]";
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(followerIds);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize followerIds to JSON", e);
        }
    }
}

