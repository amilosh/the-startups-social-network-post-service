package faang.school.postservice.config.kafka.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.kafka.topic")
public class TopicProperties {

    private Post post;

    @Getter
    @Setter
    public static class Post {
        private String name;
        private int partitions;
        private short replicationFactor;
    }
}
