package faang.school.postservice.model.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;

@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record PostRedis (
        String key,
        String title,
        String content,
        long authorId,
        String likeKey,
        String commentKey
) {
}
