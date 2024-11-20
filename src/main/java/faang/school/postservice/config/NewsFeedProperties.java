package faang.school.postservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("post.cache")
public class NewsFeedProperties {
    private int countHoursTimeToLive;
    private int newsFeedSize;
    private int limitCommentsOnPost;
}
