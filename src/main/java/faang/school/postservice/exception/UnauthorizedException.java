package faang.school.postservice.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(long userId) {
        super(MessageError.USER_UNAUTHORIZED_EXCEPTION.getMessage(userId));
    }
}
