package faang.school.postservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
public class ValidationErrorResponse {
    private final List<Violation> violations;
}
