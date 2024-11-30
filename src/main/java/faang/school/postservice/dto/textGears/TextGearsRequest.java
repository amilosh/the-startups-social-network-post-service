package faang.school.postservice.dto.textGears;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TextGearsRequest {
    private String text;
    private String key;
}
