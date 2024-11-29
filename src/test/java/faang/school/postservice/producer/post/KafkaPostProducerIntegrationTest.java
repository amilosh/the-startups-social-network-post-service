package faang.school.postservice.producer.post;

import faang.school.postservice.config.properties.kafka.KafkaProperties;
import faang.school.postservice.event.kafka.post.PostCreateEvent;
import faang.school.postservice.util.BaseContextTest;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class KafkaPostProducerIntegrationTest extends BaseContextTest {

    @Autowired
    private KafkaPostProducer kafkaPostProducer;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private KafkaProperties kafkaProperties;

    private static final Long ID = 1L;
    private PostCreateEvent event;

    @BeforeEach
    public void init() {
        event = PostCreateEvent.builder()
                .postId(ID)
                .authorId(ID)
                .subscribers(List.of(ID))
                .build();
    }

    @Test
    @DisplayName("Sending and receiving kafka message postCreated then checks it's values")
    public void whenSendingKafkaMessageCommentLikeThenDeserializeItPollAndCheck() {
        kafkaTemplate.send(kafkaProperties.getTopics().getPostCreatedTopic().getName(), event);

        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProperties.getProducerConfig().getBootstrapServersConfig());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        DefaultKafkaConsumerFactory<String, PostCreateEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProps);

        Consumer<String, PostCreateEvent> consumer = consumerFactory.createConsumer();
        consumer.subscribe(Collections.singletonList(kafkaProperties.getTopics().getPostCreatedTopic().getName()));

        ConsumerRecords<String, PostCreateEvent> records = consumer.poll(Duration.ofSeconds(3));
        consumer.close();

        assertEquals(1, records.count(), "It must be one incoming message");
        ConsumerRecord<String, PostCreateEvent> record = records.iterator().next();
        assertEquals(event.getPostId(), record.value().getPostId());
        assertEquals(event.getAuthorId(), record.value().getAuthorId());
        assertEquals(event.getSubscribers().size(), record.value().getSubscribers().size());
        assertEquals(event.getSubscribers().get(0), record.value().getSubscribers().get(0));
    }
}