package faang.school.postservice.publisher.kafka;

import faang.school.postservice.model.event.kafka.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Component
public class KafkaCommentProducer {

    private final KafkaTemplate<String, Object> multiTypeKafkaTemplate;

    @Value(value = "${spring.kafka.topics.comment}")
    private String commentTopic;

    public void sendEvent(CommentEvent commentEvent) {
        CompletableFuture<SendResult<String, Object>> future = multiTypeKafkaTemplate.send(commentTopic, commentEvent);
//        future.whenComplete((result, ex) -> {
//            System.out.println("Send message but before timeout 5 seconds");
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        while (true) {
//            System.out.println("We don't waiting, we working");
//            Thread.sleep(1000);
//        }
    }
}
