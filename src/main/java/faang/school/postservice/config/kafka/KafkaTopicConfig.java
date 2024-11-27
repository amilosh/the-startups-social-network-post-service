package faang.school.postservice.config.kafka;

import faang.school.postservice.config.kafka.properties.KafkaProperties;
import faang.school.postservice.config.kafka.properties.TopicProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    private final KafkaProperties kafkaProperties;
    private final TopicProperties topicProperties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postTopic() {
        return new NewTopic(
                topicProperties.getPost().getName(),
                topicProperties.getPost().getPartitions(),
                topicProperties.getPost().getReplicationFactor());
    }
}
