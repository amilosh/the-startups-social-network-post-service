package faang.school.postservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfig {

    @Value("${spring.data.kafka.host}")
    private String host;
    @Value("${spring.data.kafka.port}")
    private int port;
    @Value("${spring.data.kafka.retries}")
    private int retries;
    @Value("${spring.data.kafka.partitions}")
    private int partitions;
    @Value("${spring.data.kafka.replicasCount}")
    private int replicasCount;

    private final KafkaProperties kafkaProperties;

    @Bean
    public Map<String, Object> producerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "%s:%s".formatted(host, port));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);

        return props;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, String.format("%s:%s", host, port));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProps());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic postPublishTopic() {
        return TopicBuilder.name(kafkaProperties.channels().get("post-channel"))
                .partitions(partitions)
                .replicas(replicasCount)
                .build();
    }

    @Bean
    public NewTopic commentKafkaTopic() {
        return TopicBuilder
                .name(kafkaProperties.channels().get("comment-channel"))
                .partitions(partitions)
                .replicas(replicasCount)
                .build();
    }

    @Bean
    public NewTopic postForNewsFeedTopic() {
        return TopicBuilder
                .name(kafkaProperties.channels().get("post-newsfeed"))
                .partitions(partitions)
                .replicas(replicasCount)
                .build();
    }

    @Bean
    public NewTopic postObservedTopic() {
        return TopicBuilder
                .name(kafkaProperties.channels().get("post-observed"))
                .partitions(partitions)
                .replicas(replicasCount)
                .build();
    }

    @Bean
    public NewTopic postLikeEventTopic() {
        return TopicBuilder
                .name(kafkaProperties.channels().get("post-like"))
                .partitions(partitions)
                .replicas(replicasCount)
                .build();
    }
}
