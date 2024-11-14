package faang.school.postservice.dto.post;

import java.time.LocalDate;

public record PostDto(String content, Long idUser, Long idProject, LocalDate createdTime) {
}
