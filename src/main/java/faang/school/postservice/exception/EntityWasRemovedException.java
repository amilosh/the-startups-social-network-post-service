package faang.school.postservice.exception;

public class EntityWasRemovedException extends RuntimeException {
    public EntityWasRemovedException(String message) {
        super(message);
    }
}
