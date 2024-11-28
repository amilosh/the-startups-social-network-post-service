package faang.school.postservice.dto.cache.feed;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Setter
@Getter
@Builder
@ToString
public class FeedCacheDto implements Serializable {

    private Long subscriberId;
    private Set<Long> postsIds;
}
