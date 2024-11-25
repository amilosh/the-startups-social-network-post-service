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
    private ConsumerConfig consumerConfig;

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
    public static class ConsumerConfig {

        private String bootstrapServersConfig;
        private String groupId;
        private String autoOffsetReset;
        private boolean autoCommit;
        private int maxPollRecords;
        private String trustedPackages;
    }

    @Getter
    @Setter
    public static class Topics {

        private PostLikeTopic postLikeTopic;
        private CommentLikeTopic commentLikeTopic;

        @Getter
        @Setter
        public static class PostLikeTopic {

            private String name;
            private int numPartitions;
            private short replicationFactor;
        }

        @Getter
        @Setter
        public static class CommentLikeTopic {

            private String name;
            private int numPartitions;
            private short replicationFactor;
        }
    }
}
