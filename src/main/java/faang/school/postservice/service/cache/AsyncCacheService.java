package faang.school.postservice.service.cache;

import java.util.concurrent.CompletableFuture;

public interface AsyncCacheService<T> {

    CompletableFuture<T> save(String key, T value);
}
