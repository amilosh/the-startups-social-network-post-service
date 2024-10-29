package faang.school.postservice.dto.ban;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UnverifiedPostDto {
    private Long postId;
    private LocalDate date;
}
