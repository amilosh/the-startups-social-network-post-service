package faang.school.postservice.dto.textGears;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TextGearsResponse {
    private final boolean status;
    private final Response response;

    public static record Response(String corrected) {
    }
}
