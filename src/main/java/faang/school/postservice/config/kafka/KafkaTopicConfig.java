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

    @Value("${spring.data.kafka.channels.comment-nf-topic}")
    private String commentNFTopic;

    @Value("${spring.data.kafka.channels.post-view-nf-topic}")
    private String postViewNFTopic;

    @Value("${spring.data.kafka.channels.like-channel.name}")
    private String likeEvent;

    @Value("${spring.data.kafka.channels.comment-event-channel.name}")
    private String commentEvent;

    @Value("${spring.data.kafka.channels.user-ban.name}")
    private String userBanEvent;

    @Value("${spring.data.kafka.channels.post-publish-topic}")
    private String postEventChannel;

    @Value("${spring.data.kafka.channels.ad-bought-channel.name}")
    private String adBoughtEvent;

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
                .partitions(5)
                .build();
    }

    @Bean
    public NewTopic postPublishTopic() {
        return TopicBuilder
                .name(postPublishTopic)
                .build();
    }

    @Bean
    public NewTopic commentNFTopic() {
        return TopicBuilder
                .name(commentNFTopic)
                .build();
    }

    @Bean
    public NewTopic postViewNFTopic() {
        return TopicBuilder
                .name(postViewNFTopic)
                .partitions(5)
                .build();
    }

    @Bean
    public NewTopic likeTopic() {
        return TopicBuilder
                .name(likeEvent)
                .build();
    }

    @Bean
    public NewTopic userBanTopic() {
        return TopicBuilder
                .name(userBanEvent)
                .build();
    }

    @Bean
    public NewTopic commentTopic() {
        return TopicBuilder
                .name(commentEvent)
                .build();
    }

    @Bean
    public NewTopic postEventTopic() {
        return TopicBuilder
                .name(postEventChannel)
                .build();
    }

    @Bean
    public NewTopic adBoughtEventTopic() {
        return TopicBuilder
                .name(adBoughtEvent)
                .build();
    }
}
