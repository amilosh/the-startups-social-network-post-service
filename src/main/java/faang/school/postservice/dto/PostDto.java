package faang.school.postservice.dto;

import java.time.LocalDate;

public record PostDto(String content, Long idUser, Long idProject, LocalDate createdTime) {


}
