package faang.school.postservice.exception.redis;

public class RedisLockException extends RuntimeException {
    public RedisLockException(String message, Object... args) {
        super(String.format(message, args));
    }
}
