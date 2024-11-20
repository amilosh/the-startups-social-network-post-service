package faang.school.postservice.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateAndTimeFormat {

    public LocalDateTime localDateTime(LocalDateTime localDateTime) {
       return LocalDateTime.parse(localDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yy HH:mm")));
    }
}
