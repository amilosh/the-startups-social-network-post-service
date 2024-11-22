package faang.school.postservice.config.kafka;

import faang.school.postservice.config.properties.kafka.KafkaProperties;
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

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProperties.getProducerConfig().getBootstrapServersConfig());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic likeTopic() {
        return new NewTopic(kafkaProperties.getTopics().getLikeTopic().getName(),
                kafkaProperties.getTopics().getLikeTopic().getNumPartitions(),
                kafkaProperties.getTopics().getLikeTopic().getReplicationFactor());
    }
}
