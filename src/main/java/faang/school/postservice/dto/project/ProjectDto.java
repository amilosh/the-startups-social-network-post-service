package faang.school.postservice.dto.project;

import lombok.Builder;

@Builder
public record ProjectDto(
        Long id,
        String title
) {
}
