package faang.school.postservice.service.like;

import faang.school.postservice.annotations.SendPostLikeToLikesManager;
import faang.school.postservice.dto.like.LikeAction;
import faang.school.postservice.kafka.like.LikeKafkaProducer;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.post.Post;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static faang.school.postservice.dto.like.LikeAction.ADD;
import static faang.school.postservice.dto.like.LikeAction.REMOVE;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Service
public class LikesManager {
    private final LikeKafkaProducer likeKafkaProducer;

    private final ConcurrentHashMap<Long, AtomicInteger> postsLikes = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    @PreDestroy
    public void destroy() {
        publishPostLikesMapToKafka();
    }

    @Async("kafkaPublisherExecutor")
    @AfterReturning(
            pointcut = "@annotation(sendPostLikeToLikesManager)",
            returning = "returnValue"
    )
    public void publishPostEvent(Object returnValue, SendPostLikeToLikesManager sendPostLikeToLikesManager) {
        Long postId = definePostId(returnValue, sendPostLikeToLikesManager.type());
        LikeAction action = sendPostLikeToLikesManager.action();

        if (action == ADD) {
            postsLikes.computeIfAbsent(postId, id -> new AtomicInteger(0)).incrementAndGet();
        } else if (action == REMOVE) {
            postsLikes.computeIfAbsent(postId, id -> new AtomicInteger(0)).decrementAndGet();
        } else {
            throw new IllegalArgumentException("Incorrect LikeAction value");
        }
    }

    public void publishPostLikesMapToKafka() {
        lock.lock();
        try {
            if (!postsLikes.isEmpty()) {
                Map<Long, Integer> postsLikesToKafka = new HashMap<>();
                postsLikes.forEach((postId, count) -> postsLikesToKafka.put(postId, count.get()));

                likeKafkaProducer.sendPostLikeToKafka(postsLikesToKafka);

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
