package faang.school.postservice.dto.redis;

import faang.school.postservice.model.Like;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRedisDto {
    private String content;
    private long authorId;
    private Integer likes;
}
