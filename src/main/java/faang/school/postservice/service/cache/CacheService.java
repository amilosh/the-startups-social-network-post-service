package faang.school.postservice.service.cache;

import java.time.Duration;

public interface CacheService<T> {

    void put(String key, T value, Duration time);

    long incrementAndGet(String key);
}
