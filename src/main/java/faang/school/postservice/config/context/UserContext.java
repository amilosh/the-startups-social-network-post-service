package faang.school.postservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserContext {

    @Value("${tech-user-id}")
    private Long techUserId;

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        Long userId = userIdHolder.get();
        if (userId == null) {
            return techUserId;
        }
        return userId;
    }

    public void clear() {
        userIdHolder.remove();
    }
}
