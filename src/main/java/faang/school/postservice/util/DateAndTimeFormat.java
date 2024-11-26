package faang.school.postservice.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateAndTimeFormat {

    public LocalDateTime localDateTime(LocalDateTime localDateTime) {
       return localDateTime.withNano(0);
    }
}
