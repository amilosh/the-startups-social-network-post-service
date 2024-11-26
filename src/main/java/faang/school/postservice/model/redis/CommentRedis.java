package faang.school.postservice.model.redis;

import lombok.Builder;

@Builder
public record CommentRedis(
        String key,
        long commentAuthorId,
        String content,
        String likeKey
) {
}
