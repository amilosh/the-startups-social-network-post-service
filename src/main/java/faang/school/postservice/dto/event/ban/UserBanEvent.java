package faang.school.postservice.dto.event.ban;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserBanEvent {

    private final Long userId;
}
