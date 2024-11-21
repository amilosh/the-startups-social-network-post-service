package faang.school.postservice.config.properties.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfigurationProperties {

    private Producer producer;
    private Topic topic;

    @Getter
    @Setter
    public static class Producer {

        private String bootstrapAddress;
        private int partition;
        private short replicationFactor;
        private String acks;
        private int retries;
        private boolean idempotence;
    }

    @Getter
    @Setter
    public static class Topic {

        private String likeTopic;
    }
}
