package faang.school.postservice.publisher.kafka.publishers.util.builder;

import faang.school.postservice.dto.post.message.LikeForPostMessage;
import faang.school.postservice.model.Like;
import org.springframework.stereotype.Component;

@Component
public class LikeForPostMessageBuilder {
    public LikeForPostMessage build(Like like) {
        return LikeForPostMessage.builder()
                .postId(like.getPost().getId())
                .build();
    }
}
