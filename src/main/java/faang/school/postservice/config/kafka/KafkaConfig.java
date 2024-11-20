package faang.school.postservice.config.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaProperties.class)
@Slf4j
public class KafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler((consumerRecord, e) -> {
            log.error("Failed to process record: {}, exception: {}", consumerRecord.value(), e.getMessage());
        }, new FixedBackOff(kafkaProperties.interval(), kafkaProperties.retries()));
    }

    @Bean
    public NewTopic postPublishTopic() {
        return new NewTopic(kafkaProperties.channels().get("post-channel"), 1, (short) 1);
    }

    @Bean
    public NewTopic likePublishTopic() {
        return new NewTopic(kafkaProperties.channels().get("like-channel"), 1, (short) 1);
    }
}
