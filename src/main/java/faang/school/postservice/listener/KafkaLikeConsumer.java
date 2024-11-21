package faang.school.postservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.message.LikePublishMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeConsumer {

    private final ObjectMapper mapper;

    @KafkaListener(topics = "${spring.kafka.topic.like-publisher}")
    public void handlerNewLike(LikePublishMessage message, Acknowledgment ack) {
        log.info("Received like publish message: {}", message);

        LikePublishMessage likePublishMessage = mapper.convertValue(message, LikePublishMessage.class);

        ack.acknowledge();
    }
}
