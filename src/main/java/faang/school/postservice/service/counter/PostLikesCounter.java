package faang.school.postservice.service.counter;

import faang.school.postservice.annotations.SendUserActionToCounter;
import faang.school.postservice.kafka.like.UserActionKafkaProducer;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.service.counter.enumeration.ChangeType;
import faang.school.postservice.service.counter.enumeration.UserAction;
import jakarta.annotation.PreDestroy;
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
public class PostLikesCounter implements UserActionCounter {
    private final UserAction userAction = UserAction.POST_LIKE;
    private final ConcurrentHashMap<Long, AtomicInteger> postsLikes = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    private final UserActionKafkaProducer userActionKafkaProducer;

    @PreDestroy
    public void destroy() {
        publishPostLikesMapToKafka();
    }

    @Override
    public UserAction getUserAction() {
        return userAction;
    }

    public void executeCounting(Object returnValue, SendUserActionToCounter sendUserActionToCounter) {
        Long postId = definePostId(returnValue, sendUserActionToCounter.type());
        ChangeType changeType = sendUserActionToCounter.changeType();

        if (changeType == INCREMENT) {
            postsLikes.computeIfAbsent(postId, id -> new AtomicInteger(0)).incrementAndGet();
        } else if (changeType == DECREMENT) {
            postsLikes.computeIfAbsent(postId, id -> new AtomicInteger(0)).decrementAndGet();
        } else {
            throw new IllegalArgumentException("Incorrect ChangeType value");
        }
    }

    public void publishPostLikesMapToKafka() {
        lock.lock();
        try {
            if (!postsLikes.isEmpty()) {
                Map<Long, Integer> postsLikesToKafka = new HashMap<>();
                postsLikes.forEach((postId, count) -> postsLikesToKafka.put(postId, count.get()));

                userActionKafkaProducer.sendPostLikesMapToKafka(postsLikesToKafka);

                postsLikes.clear();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    private Long definePostId(Object returnValue, Class<?> type) {
        Long postId;
        if (type == Like.class) {
            Like like = (Like) returnValue;
            postId = like.getPost().getId();
        } else if (type == Post.class) {
            Post post = (Post) returnValue;
            postId = post.getId();
        } else {
            throw new IllegalArgumentException("Incorrect data type");
        }
        return postId;
    }
}
