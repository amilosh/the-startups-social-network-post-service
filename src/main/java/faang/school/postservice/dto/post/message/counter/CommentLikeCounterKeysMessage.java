package faang.school.postservice.dto.post.message.counter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentLikeCounterKeysMessage {
    private List<String> commentLikeCounterKeys;
}
