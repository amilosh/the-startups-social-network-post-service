package faang.school.postservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserContext {

    @Value("${user-service.service-user:1}")
    private long serviceUser;

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        Long userId = userIdHolder.get();
        if (userId == null) {
            return serviceUser;
        }
        return userId;
    }

    public void clear() {
        userIdHolder.remove();
    }
}
