package faang.school.postservice.mapper;

import faang.school.postservice.model.CacheableUser;
import faang.school.postservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    CacheableUser toCacheable(User user);
}
