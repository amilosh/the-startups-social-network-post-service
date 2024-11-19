package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsFactory {
    @Value("${kafka.partitions}")
    private int partitions;

    @Value("${kafka.replicas}")
    private int replicas;

    @Value("${kafka.topic.post-published-topic}")
    private String postPublishedTopic;

    @Bean
    public NewTopic postPublishedTopic() {
        return TopicBuilder.name(postPublishedTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
