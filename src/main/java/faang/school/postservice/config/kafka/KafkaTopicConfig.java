package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topics.post}")
    private String postTopic;

    @Value("${kafka.topics.like}")
    private String likeTopic;

    @Bean
    public NewTopic postsTopic() {
        return new NewTopic(postTopic, 3, (short) 1);
    }
    @Bean
    public NewTopic likesTopic() {
        return new NewTopic(postTopic, 3, (short) 1);
    }
}