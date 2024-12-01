package faang.school.postservice.excaption.post;

public class PostException extends RuntimeException {
    public PostException(String message, Object... args) {
        super(String.format(message, args));
    }

}
