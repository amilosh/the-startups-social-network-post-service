package faang.school.postservice.dto.post.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersFeedUpdateMessage {
    private List<Long> userIds;
}
