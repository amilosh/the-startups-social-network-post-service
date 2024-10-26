package faang.school.postservice.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private Map<String, String> errorFields;
    private String message;

    public void addError(String errorField, String message) {
        errorFields.put(errorField, message);
    }
}
