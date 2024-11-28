package faang.school.postservice.exception.kafka;

public class KafkaPublishPostException extends RuntimeException {
    public KafkaPublishPostException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
