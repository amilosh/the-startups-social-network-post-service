package faang.school.postservice.dto.resource;

import java.time.LocalDateTime;

public record ResourceDto(
        String key,
        long size,
        LocalDateTime createdAt,
        String name,
        String type,
        Long postId
) {
}
