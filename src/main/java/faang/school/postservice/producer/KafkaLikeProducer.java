package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.message.LikePublishMessage;
import faang.school.postservice.model.Like;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaLikeProducer extends KafkaEventProducer {

    public KafkaLikeProducer(KafkaTemplate<String, String> kafkaTemplate,
                             ObjectMapper mapper,
                             @Value("${spring.kafka.topic.like-publisher}") String likePostTopic) {
        super(kafkaTemplate, likePostTopic, mapper);
    }

    public void publishLike(Like like) {
        LikePublishMessage likePublishMessage = new LikePublishMessage();
        likePublishMessage.setPostId(like.getPost().getId());
        likePublishMessage.setUserId(like.getUserId());

        publishEvent(likePublishMessage);
    }
}
