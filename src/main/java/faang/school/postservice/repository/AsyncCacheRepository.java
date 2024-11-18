package faang.school.postservice.repository;

import java.util.concurrent.CompletableFuture;

public interface AsyncCacheRepository<T> {

    CompletableFuture<T> save(String key, T value);
}
