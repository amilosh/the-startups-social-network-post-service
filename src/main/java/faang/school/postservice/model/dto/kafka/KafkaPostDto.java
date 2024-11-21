package faang.school.postservice.model.dto.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.postservice.model.enums.AuthorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KafkaPostDto {
    private Long authorId;
    private AuthorType authorType;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}