package faang.school.postservice.validator.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.AlreadyExistsException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LikeValidatorTest {

    private final LikeValidator likeValidator = new LikeValidator();

    @Test
    public void validateLikeHasTargetThrowExceptionTest() {
        Long firstId = null;
        Long second = null;

        assertThrows(DataValidationException.class,
                () -> likeValidator.validateLikeHasTarget(firstId, second));
    }

    @Test
    public void validateLikeHasTargetShouldNotThrowExceptionPostIdIsProvidedTest() {
        Long firstId = 1L;
        Long second = null;

        assertDoesNotThrow(() -> likeValidator.validateLikeHasTarget(firstId, second));
    }

    @Test
    public void validateLikeHasTargetShouldNotThrowExceptionCommentIdIsProvidedTest() {
        Long firstId = null;
        Long second = 1L;

        assertDoesNotThrow(() -> likeValidator.validateLikeHasTarget(firstId, second));
    }

    @Test
    public void validateLikeHasTargetShouldNotThrowExceptionPostIdAndCommentIdAreProvidedTest() {
        Long firstId = 1L;
        Long second = 1L;

        assertDoesNotThrow(() -> likeValidator.validateLikeHasTarget(firstId, second));
    }

    @Test
    public void validateUserAddOnlyOneLikeToPostThrowsExceptionTest() {
        long userId = 1L;
        Long firstLikeUserId = 1L;
        Long secondLikeUserId = 2L;

        Like like1 = Like.builder().userId(firstLikeUserId).build();
        Like like2 = Like.builder().userId(secondLikeUserId).build();
        List<Like> likes = new ArrayList<>(List.of(like1, like2));

        assertThrows(DataValidationException.class,
                () -> likeValidator.validateUserAddOnlyOneLikeToPost(likes, userId));
    }

    @Test
    public void validateUserAddOnlyOneLikeToPostDoNotThrowsExceptionTest() {
        long userId = 1L;
        Long firstLikeUserId = 2L;
        Long secondLikeUserId = 3L;

        Like like1 = Like.builder().userId(firstLikeUserId).build();
        Like like2 = Like.builder().userId(secondLikeUserId).build();
        List<Like> likes = new ArrayList<>(List.of(like1, like2));

        assertDoesNotThrow(() ->
                likeValidator.validateUserAddOnlyOneLikeToPost(likes, userId));
    }

    @Test
    public void validateUserAddOnlyOneLikeToCommentThrowsExceptionTest() {
        long userId = 1L;
        Long firstLikeUserId = 1L;
        Long secondLikeUserId = 2L;

        Like like1 = Like.builder().userId(firstLikeUserId).build();
        Like like2 = Like.builder().userId(secondLikeUserId).build();
        List<Like> likes = new ArrayList<>(List.of(like1, like2));

        assertThrows(DataValidationException.class,
                () -> likeValidator.validateUserAddOnlyOneLikeToComment(likes, userId));
    }

    @Test
    public void validateUserAddOnlyOneLikeToCommentDoNotThrowsExceptionTest() {
        long userId = 1L;
        Long firstLikeUserId = 2L;
        Long secondLikeUserId = 3L;

        Like like1 = Like.builder().userId(firstLikeUserId).build();
        Like like2 = Like.builder().userId(secondLikeUserId).build();
        List<Like> likes = new ArrayList<>(List.of(like1, like2));

        assertDoesNotThrow(() ->
                likeValidator.validateUserAddOnlyOneLikeToComment(likes, userId));
    }

    @Test
    public void validateLikeWasNotPutToCommentDtoCommentIdNullLikeNullTest() {
        LikeDto likeDto = LikeDto.builder().commentId(null).build();
        Like like = null;

        assertDoesNotThrow(() -> likeValidator.validateLikeWasNotPutToComment(likeDto, like));
    }

    @Test
    public void validateLikeWasNotPutToCommentDtoCommentIdNullLikeCommentNullTest() {
        LikeDto likeDto = LikeDto.builder().commentId(null).build();
        Like like = Like.builder().comment(null).build();

        assertDoesNotThrow(() -> likeValidator.validateLikeWasNotPutToComment(likeDto, like));
    }

    @Test
    public void validateLikeWasNotPutToCommentCommentIdNotNullShouldTrowExceptionTest() {
        long commentId = 1L;
        LikeDto likeDto = LikeDto.builder().commentId(commentId).build();
        Like like = Like.builder().comment(null).build();

        assertThrows(AlreadyExistsException.class,
                () -> likeValidator.validateLikeWasNotPutToComment(likeDto, like));
    }

    @Test
    public void validateLikeWasNotPutToCommentCommentNotNullShouldTrowExceptionTest() {
        LikeDto likeDto = LikeDto.builder().commentId(null).build();
        Like like = Like.builder().comment(new Comment()).build();

        assertThrows(AlreadyExistsException.class,
                () -> likeValidator.validateLikeWasNotPutToComment(likeDto, like));
    }

    @Test
    public void validateLikeWasNotPutToPostDtoPostIdNullLikeNullTest() {
        LikeDto likeDto = LikeDto.builder().postId(null).build();
        Like like = null;

        assertDoesNotThrow(() -> likeValidator.validateLikeWasNotPutToPost(likeDto, like));
    }

    @Test
    public void validateLikeWasNotPutToPostDtoPostIdNullLikePostNullTest() {
        LikeDto likeDto = LikeDto.builder().postId(null).build();
        Like like = Like.builder().post(null).build();

        assertDoesNotThrow(() -> likeValidator.validateLikeWasNotPutToPost(likeDto, like));
    }

    @Test
    public void validateLikeWasNotPutToPostPostIdNotNullShouldTrowExceptionTest() {
        long commentId = 1L;
        LikeDto likeDto = LikeDto.builder().postId(commentId).build();
        Like like = Like.builder().post(null).build();

        assertThrows(AlreadyExistsException.class,
                () -> likeValidator.validateLikeWasNotPutToPost(likeDto, like));
    }

    @Test
    public void validateLikeWasNotPutToPostPostNotNullShouldTrowExceptionTest() {
        LikeDto likeDto = LikeDto.builder().postId(null).build();
        Like like = Like.builder().post(new Post()).build();

        assertThrows(AlreadyExistsException.class,
                () -> likeValidator.validateLikeWasNotPutToPost(likeDto, like));
    }
}