package faang.school.postservice.config.kafka;

import faang.school.postservice.config.properties.kafka.KafkaProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProperties.getConsumerConfig().getBootstrapServersConfig());

        configProps.put(ConsumerConfig.GROUP_ID_CONFIG,
                kafkaProperties.getConsumerConfig().getGroupId());

        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                kafkaProperties.getConsumerConfig().getAutoOffsetReset());

        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                kafkaProperties.getConsumerConfig().isEnableAutoCommit());

        configProps.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
                kafkaProperties.getConsumerConfig().getInterval());

        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
                kafkaProperties.getConsumerConfig().getMaxPollRecords());

        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,
                kafkaProperties.getConsumerConfig().getSessionTimeout());

        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);

        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);

        configProps.put(JsonDeserializer.TRUSTED_PACKAGES,
                kafkaProperties.getConsumerConfig().getTrustedPackages());

        return new DefaultKafkaConsumerFactory<>(configProps,
                new StringDeserializer(), new JsonDeserializer<>());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }
}
