package faang.school.postservice.model.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRedis implements Serializable {
    private Long id;
    private String content;
    private Long authorId;
    private Integer likes;
    private Integer views;
    private List<Long> comments;
    private LocalDateTime publishedAt;
}
