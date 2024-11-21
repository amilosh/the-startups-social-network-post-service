package faang.school.postservice.model.event.kafka;

import faang.school.postservice.model.dto.kafka.KafkaPostDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostCreatedEvent {
    private KafkaPostDto kafkaPostDto;
    private List<Long> followerIds;
}
