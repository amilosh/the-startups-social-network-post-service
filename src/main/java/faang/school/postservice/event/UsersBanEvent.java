package faang.school.postservice.event;

import java.util.List;

public record UsersBanEvent(
        List<Long> userIdsToBan
) {
}
