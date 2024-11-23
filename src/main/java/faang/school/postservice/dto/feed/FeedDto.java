package faang.school.postservice.dto.feed;

import lombok.Builder;

import java.util.List;

@Builder
public record FeedDto (
    long postId,
    String content,
    long authorId,
    List<Long> likeIds,
    List<Long> commentIds
) {}
