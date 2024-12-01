package faang.school.postservice.repository.cache.util.key;

import org.springframework.stereotype.Component;

@Component
public class CacheKey {
    public String buildKey(String prefix, long id, String postfix) {
        return prefix + id + (postfix != null ? postfix : "");
    }
}
