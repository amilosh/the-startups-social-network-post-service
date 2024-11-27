package faang.school.postservice.event.kafka;

import faang.school.postservice.config.context.UserContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class LikeKafkaEventBuilder {

    public static <T> T buildEvent(Class<T> eventClass, long authorId, long typeId, UserContext userContext) {
        try {
            return eventClass.getDeclaredConstructor(Long.class, Long.class, Long.class, LocalDateTime.class)
                    .newInstance(
                            authorId,
                            userContext.getUserId(),
                            typeId,
                            LocalDateTime.now()
                    );
        } catch (Exception e) {
            log.error("Error while building event {}", eventClass.getName());
            throw new RuntimeException("Error creating Kafka event!", e);
        }
    }
}
