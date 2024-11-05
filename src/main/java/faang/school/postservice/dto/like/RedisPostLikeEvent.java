package faang.school.postservice.dto.like;

import lombok.Data;

@Data
public class RedisPostLikeEvent {
    private Long likeAuthorId;
    private Long postId;
    private Long postAuthorId;
}
