package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsFactory {
    @Value("${spring.data.kafka.topics.posts:posts}")
    private String postsTopic;

    @Bean
    public NewTopic posts() {
        return TopicBuilder.name(postsTopic).build();
    }
}
