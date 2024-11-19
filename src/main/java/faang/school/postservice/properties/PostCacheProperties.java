package faang.school.postservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@Data
@ConfigurationProperties(prefix = "cache.post")
public class PostCacheProperties {
    private int liveTime;
    private String timeUnit;
    private String setKey;

    public TimeUnit getTimeUnit() {
        return TimeUnit.valueOf(timeUnit);
    }
}
