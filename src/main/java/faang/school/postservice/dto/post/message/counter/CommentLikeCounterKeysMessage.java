package faang.school.postservice.dto.post.message.counter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentLikeCounterKeysMessage {
    private List<String> commentLikeCounterKeys;
}
