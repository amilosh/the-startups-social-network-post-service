package faang.school.postservice.model.dto.redis.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RedisUserDto {
    private Long userId;
    private String username;
    private String fileId;
    private String smallFileId;
    private List<Long> followerIds;
}
