package faang.school.postservice.producer.like.comment;

import faang.school.postservice.config.properties.kafka.KafkaProperties;
import faang.school.postservice.event.kafka.comment.like.CommentLikeKafkaEvent;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KafkaCommentLikeProducerIntegrationTest extends BaseContextTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Autowired
    private KafkaCommentLikeProducer kafkaCommentLikeProducer;

    private CommentLikeKafkaEvent commentLikeKafkaEvent;

    @BeforeEach
    void setUp() {
        commentLikeKafkaEvent = CommentLikeKafkaEvent.builder()
                .commentAuthorId(1L)
                .likeAuthorId(2L)
                .commentId(3L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Sending and receiving kafka message comment like then checks it's values")
    public void whenSendingKafkaMessageCommentLikeThenDeserializeItPollAndCheck() {
        kafkaTemplate.send(kafkaProperties.getTopics().getCommentLikeTopic().getName(), commentLikeKafkaEvent);

        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaProperties.getProducerConfig().getBootstrapServersConfig());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        DefaultKafkaConsumerFactory<String, CommentLikeKafkaEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProps);

        Consumer<String, CommentLikeKafkaEvent> consumer = consumerFactory.createConsumer();
        consumer.subscribe(Collections.singletonList(kafkaProperties.getTopics().getCommentLikeTopic().getName()));

        ConsumerRecords<String, CommentLikeKafkaEvent> records = consumer.poll(Duration.ofSeconds(3));
        consumer.close();

        assertEquals(1, records.count(), "It must be one incoming message");
        ConsumerRecord<String, CommentLikeKafkaEvent> record = records.iterator().next();
        assertEquals(commentLikeKafkaEvent.getCreatedAt(), record.value().getCreatedAt());
        assertEquals(commentLikeKafkaEvent.getCommentId(), record.value().getCommentId());
        assertEquals(commentLikeKafkaEvent.getLikeAuthorId(), record.value().getLikeAuthorId());
        assertEquals(commentLikeKafkaEvent.getCommentAuthorId(), record.value().getCommentAuthorId());
    }
}
