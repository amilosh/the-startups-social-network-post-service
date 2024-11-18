package faang.school.postservice.dto.post.message.counter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostViewCountersKeysMessage {
    private List<String> viewCountersKeys;
}
