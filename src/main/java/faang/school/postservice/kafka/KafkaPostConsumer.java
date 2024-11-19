package faang.school.postservice.kafka;

import faang.school.postservice.kafka.dto.PostKafkaDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaPostConsumer {
    @KafkaListener(
            topics = "${kafka.topic.post-published-topic}",
            groupId = "${kafka.consumer.group-id}")
    public void handle(PostKafkaDto postKafkaDto) {
        log.info("postKafkaDto: " + postKafkaDto.toString());
    }
}
