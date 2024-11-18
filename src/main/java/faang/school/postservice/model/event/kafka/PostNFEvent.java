package faang.school.postservice.model.event.kafka;

import lombok.Builder;

import java.util.List;

@Builder
public record PostNFEvent(
        long postId,
        List<Long> followersId) {
}
