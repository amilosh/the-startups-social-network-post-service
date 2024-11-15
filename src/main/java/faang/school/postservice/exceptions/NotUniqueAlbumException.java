package faang.school.postservice.exceptions;

public class NotUniqueAlbumException extends RuntimeException {
    public NotUniqueAlbumException(String message) {
        super(message);
    }
}
