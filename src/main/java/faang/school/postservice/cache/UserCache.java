package faang.school.postservice.cache;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;


@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("UserCache")
public class UserCache implements Serializable {
    private Long id;
    private String username;
    private String avatarSmall;

    @TimeToLive
    private Long ttl;
}
