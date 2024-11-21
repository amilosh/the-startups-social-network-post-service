package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.data.kafka.channels.post-channel}")
    private String postNFTopic;

    @Value("${spring.data.kafka.channels.like-nf-channel}")
    private String likeNFTopic;

    @Value("${spring.data.kafka.channels.post-publish-topic}")
    private String postPublishTopic;

    @Bean
    public NewTopic postNFTopic() {
        return TopicBuilder
                .name(postNFTopic)
                .build();
    }

    @Bean
    public NewTopic likeNFTopic() {
        return TopicBuilder
                .name(likeNFTopic)
                .build();
    }

    @Bean
    public NewTopic postPublishTopic() {
        return TopicBuilder
                .name(postPublishTopic)
                .build();
    }
}
