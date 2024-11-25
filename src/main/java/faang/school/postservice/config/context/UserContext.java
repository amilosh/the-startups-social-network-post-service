package faang.school.postservice.config.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserContext {
    @Value("${app.service-user.id}")
    private long serviceUsrId;

    private final ThreadLocal<Long> userIdHolder = new ThreadLocal<>();

    public void setUserId(long userId) {
        userIdHolder.set(userId);
    }

    public long getUserId() {
        Long userId = userIdHolder.get();
        if (userId == null) {
            throw new IllegalArgumentException("User ID is missing. Please make sure 'x-user-id' header is included in the request.");
        }
        return userId;
    }

    public long getUserIdForFeignRequest() {
        Long userId = userIdHolder.get();
        if (userId == null) {
            return serviceUsrId;
        } else {
            return userId;
        }
    }

    public void clear() {
        userIdHolder.remove();
    }
}
