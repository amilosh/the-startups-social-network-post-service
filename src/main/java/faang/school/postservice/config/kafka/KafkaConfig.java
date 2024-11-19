package faang.school.postservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    @Value(value = "${spring.kafka.topic.post-publisher}")
    private String publishPostTopicName;

    @Bean
    public NewTopic publishPostTopic() {
        return new NewTopic(publishPostTopicName, 1, (short) 1);
    }
}
