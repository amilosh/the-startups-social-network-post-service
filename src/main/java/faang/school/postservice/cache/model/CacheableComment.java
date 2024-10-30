package faang.school.postservice.cache.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CacheableComment implements Comparable<CacheableComment> {
    private Long id;
    private String content;
    private CacheableUser author;
    private Long postId;

    @Override
    public int compareTo(CacheableComment other) {
        return other.id.compareTo(this.id);
    }
}
