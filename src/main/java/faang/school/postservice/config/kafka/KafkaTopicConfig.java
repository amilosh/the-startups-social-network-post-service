package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.data.kafka.channels.post-channel}")
    private String postChannel;

    @Bean
    public NewTopic postNFTopic() {
        return TopicBuilder
                .name(postChannel)
                .build();
    }
}
