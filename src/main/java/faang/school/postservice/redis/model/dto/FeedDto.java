package faang.school.postservice.redis.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FeedDto {
    private Long id;
    private List<PostRedisDto> postRedisDtos;
}
