package faang.school.postservice.publisher.kafka.publishers.util.builder;

import faang.school.postservice.dto.post.message.LikeForCommentMessage;
import faang.school.postservice.model.Like;
import org.springframework.stereotype.Component;

@Component
public class LikeForCommentMessageBuilder {
    public LikeForCommentMessage build(Like like) {
        return LikeForCommentMessage.builder()
                .postId(like.getComment().getPost().getId())
                .commentId(like.getComment().getId())
                .build();
    }
}
