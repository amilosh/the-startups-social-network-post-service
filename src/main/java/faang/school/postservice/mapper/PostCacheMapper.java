package faang.school.postservice.mapper;

import faang.school.postservice.cache.PostCache;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper (componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostCacheMapper {

    PostCache toPostCache(Post post);
}
