package faang.school.postservice.service.cache;

public interface ListCacheService<T> {

    void rightPush(String listKey, T value);
}
