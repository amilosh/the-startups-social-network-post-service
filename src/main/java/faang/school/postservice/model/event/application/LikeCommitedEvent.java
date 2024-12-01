package faang.school.postservice.model.event.application;

import faang.school.postservice.model.dto.LikeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeCommitedEvent {
    private LikeDto likeDto;
}
