package faang.school.postservice.publisher.kafka.publishers.util.builder;

import faang.school.postservice.dto.post.message.ViewPostMessage;
import faang.school.postservice.dto.post.serializable.PostViewEventParticipant;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ViewPostMessageBuilder {
    public ViewPostMessage build(List<PostViewEventParticipant> posts) {
        List<Long> postIds = posts.stream()
                .map(PostViewEventParticipant::getId)
                .toList();

        return ViewPostMessage.builder()
                .postsIds(postIds)
                .build();
    }
}
