package faang.school.postservice.dto.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostEvent {
    private Long id;
    private Long authorId;
    private List<Long> subscribersId;
}
