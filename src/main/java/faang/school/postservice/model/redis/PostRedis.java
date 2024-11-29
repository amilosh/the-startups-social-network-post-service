package faang.school.postservice.model.redis;

import faang.school.postservice.model.event.kafka.PostCommentEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRedis {
    private long id;
    private String title;
    private String content;
    private Long authorId;
    private Long likes;
    private LinkedHashSet<PostCommentEvent> comments;
    private Long views;
}
