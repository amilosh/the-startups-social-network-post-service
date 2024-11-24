package faang.school.postservice.mapper;

import com.google.protobuf.Timestamp;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public interface DateTimeMapper {
    default Timestamp localDateTimeToTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return Timestamp.getDefaultInstance();
        }
        return Timestamp.newBuilder()
                .setSeconds(localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
                .setNanos(localDateTime.getNano())
                .build();
    }

    default LocalDateTime mapTimestampToTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return LocalDateTime.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos(), ZoneOffset.UTC);
    }
}