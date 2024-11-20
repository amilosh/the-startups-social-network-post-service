package faang.school.postservice.service.cache;

import java.util.Optional;

public interface SortedSetCacheService<T> {

    void put(String key, T value, double score);

    Optional<T> popMin(String sortedSetKey, Class<T> clazz);

    long size(String key);

    void runInOptimisticLock(Runnable task, String key);
}
