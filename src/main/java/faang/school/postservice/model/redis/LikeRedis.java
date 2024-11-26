package faang.school.postservice.model.redis;

import lombok.Builder;

@Builder
public record LikeRedis(
        long likeAuthorId)
{
}
