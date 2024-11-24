package faang.school.postservice.model.dto.redis.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RedisUserDto {
    private Long userId;
    private String username;
    private String fileId;
    private String smallFileId;
}
