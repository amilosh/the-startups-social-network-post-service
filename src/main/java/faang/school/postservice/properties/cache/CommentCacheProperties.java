package faang.school.postservice.properties.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@Data
@ConfigurationProperties(prefix = "cache.comment")
public class CommentCacheProperties {
    private int liveTime;
    private TimeUnit timeUnit;
    private String setKey;
}
