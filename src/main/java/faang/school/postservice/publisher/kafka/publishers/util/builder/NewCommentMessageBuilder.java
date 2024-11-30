package faang.school.postservice.publisher.kafka.publishers.util.builder;

import faang.school.postservice.dto.post.message.NewCommentMessage;
import faang.school.postservice.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class NewCommentMessageBuilder {
    public NewCommentMessage build(Comment comment) {
        return NewCommentMessage.builder()
                .postId(comment.getPost().getId())
                .build();
    }
}
