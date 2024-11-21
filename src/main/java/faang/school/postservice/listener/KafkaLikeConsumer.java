package faang.school.postservice.listener;

import faang.school.postservice.model.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {

    @KafkaListener(topics = "like_channel", groupId = "like_event")
    public void onLikeEvent(LikeEvent likeEvent) {

    }
}
