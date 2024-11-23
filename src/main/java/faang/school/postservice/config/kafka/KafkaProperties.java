package faang.school.postservice.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    private Consumer consumer;
    private Topic.Post post;
    private String bootstrapServers;


    @Getter
    @Setter
    protected static class Consumer {
        private long groupId;
        private String autoOffsetReset;
    }

    @Getter
    @Setter
    protected static class Topic {

        @Getter
        @Setter
        protected static class Post {
            private String name;
            private int partitions;
            private short replicationFactor;
        }
    }
}
