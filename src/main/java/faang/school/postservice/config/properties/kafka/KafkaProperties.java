package faang.school.postservice.config.properties.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.data.kafka")
public class KafkaProperties {

    private ProducerConfig producerConfig;
    private Topics topics;

    @Getter
    @Setter
    public static class ProducerConfig {

        private String bootstrapServersConfig;
        private String acks;
        private int retries;
        private boolean idempotence;
    }

    @Getter
    @Setter
    public static class Topics {

        private Topic postsTopic;

        @Getter
        @Setter
        public static class Topic {

            private String name;
            private int numPartitions;
            private short replicationFactor;
        }
    }
}
