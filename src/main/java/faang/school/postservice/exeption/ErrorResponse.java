package faang.school.postservice.exeption;

import lombok.Builder;

@Builder
public record ErrorResponse(String errorMessage) {
}
