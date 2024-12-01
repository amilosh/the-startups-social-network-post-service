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

    @Value("${kafka.topic.post-published-topic.name}")
    private String postPublishedTopic;

    @Value("${kafka.topic.post-viewed-topic}")
    private String postViewedTopic;

    @Value("${kafka.topic.post-liked-topic}")
    private String postLikedTopic;

    @Value("${kafka.topic.comment-liked-topic}")
    private String commentLikedTopic;

    @Value("${kafka.topic.comment-created-topic}")
    private String commentCreatedTopic;

    @Bean
    public NewTopic postPublishedTopic() {
        return TopicBuilder.name(postPublishedTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic postViewedTopic() {
        return TopicBuilder.name(postViewedTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic postLikedTopic() {
        return TopicBuilder.name(postLikedTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic commentLikedTopic() {
        return TopicBuilder.name(commentLikedTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic commentCreatedTopic() {
        return TopicBuilder.name(commentCreatedTopic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
