package faang.school.postservice.listener;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.kafka.support.Acknowledgment;

public interface KafkaEventListener<T> {

    void onMessage(T t, Acknowledgment acknowledgment) throws InvalidProtocolBufferException;
}
