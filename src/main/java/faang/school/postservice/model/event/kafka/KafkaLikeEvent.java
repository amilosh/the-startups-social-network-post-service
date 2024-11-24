package faang.school.postservice.model.event.kafka;

import faang.school.postservice.model.dto.kafka.KafkaLikeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KafkaLikeEvent {
    private KafkaLikeDto kafkaLikeDto;
}
