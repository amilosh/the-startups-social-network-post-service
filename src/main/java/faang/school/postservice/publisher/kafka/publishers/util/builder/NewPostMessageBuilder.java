package faang.school.postservice.publisher.kafka.publishers.util.builder;

import faang.school.postservice.dto.post.message.NewPostMessage;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;

@Component
public class NewPostMessageBuilder {
    public NewPostMessage build(Post post, List<Long> followerIds) {
        return NewPostMessage.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .createdAtTimestamp(post.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli())
                .followersIds(followerIds)
                .build();
    }
}
