package faang.school.postservice.service.counter;

import faang.school.postservice.annotations.SendUserActionToCounter;
import faang.school.postservice.kafka.like.UserActionKafkaProducer;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.comment.Comment;
import faang.school.postservice.service.counter.enumeration.ChangeType;
import faang.school.postservice.service.counter.enumeration.UserAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static faang.school.postservice.service.counter.enumeration.ChangeType.DECREMENT;
import static faang.school.postservice.service.counter.enumeration.ChangeType.INCREMENT;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommentLikesCounter implements UserActionCounter {
    private final UserAction userAction = UserAction.COMMENT_LIKE;
    private final ConcurrentHashMap<Long, AtomicInteger> commentsLikes = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    private final UserActionKafkaProducer userActionKafkaProducer;

    @Override
    public UserAction getUserAction() {
        return userAction;
    }

    @Override
    public void executeCounting(Object returnValue, SendUserActionToCounter sendUserActionToCounter) {
        Long commentId = defineCommentId(returnValue, sendUserActionToCounter.type());
        ChangeType changeType = sendUserActionToCounter.changeType();

        if (changeType == INCREMENT) {
            commentsLikes.computeIfAbsent(commentId, id -> new AtomicInteger(0)).incrementAndGet();
        } else if (changeType == DECREMENT) {
            commentsLikes.computeIfAbsent(commentId, id -> new AtomicInteger(0)).decrementAndGet();
        } else {
            throw new IllegalArgumentException("Incorrect ChangeType value");
        }
    }

    public void publishCommentLikesMapToKafka() {
        lock.lock();
        try {
            if (!commentsLikes.isEmpty()) {
                Map<Long, Integer> postsLikesToKafka = new HashMap<>();
                commentsLikes.forEach((postId, count) -> postsLikesToKafka.put(postId, count.get()));

                userActionKafkaProducer.sendCommentLikesMapToKafka(postsLikesToKafka);

                commentsLikes.clear();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    private Long defineCommentId(Object returnValue, Class<?> type) {
        Long commentId;
        if (type == Like.class) {
            Like like = (Like) returnValue;
            commentId = like.getComment().getId();
        } else if (type == Comment.class) {
            Comment comment = (Comment) returnValue;
            commentId = comment.getId();
        } else {
            throw new IllegalArgumentException("Incorrect data type");
        }
        return commentId;
    }
}
