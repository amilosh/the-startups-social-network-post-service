package faang.school.postservice.model.event.kafka;

import faang.school.postservice.model.dto.PostDto;

import java.util.List;

public class PostCreatedEvent {
    private PostDto postDto;
    private List<Long> followerIds;
}
