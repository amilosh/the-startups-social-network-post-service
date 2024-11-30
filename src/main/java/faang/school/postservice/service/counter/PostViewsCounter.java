package faang.school.postservice.service.counter;

import faang.school.postservice.annotations.SendUserActionToCounter;
import faang.school.postservice.dto.feed.PostFeedResponseDto;
import faang.school.postservice.kafka.like.UserActionKafkaProducer;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.service.counter.enumeration.UserAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostViewsCounter implements UserActionCounter {
    private final UserAction userAction = UserAction.POST_VIEW;
    private final ConcurrentHashMap<Long, AtomicInteger> postViews = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    private final UserActionKafkaProducer userActionKafkaProducer;

    @Override
    public UserAction getUserAction() {
        return userAction;
    }

    @Override
    public void executeCounting(Object returnValue, SendUserActionToCounter sendUserActionToCounter) {
        Class<?> clazz = sendUserActionToCounter.type();
        Class<?> elementType = sendUserActionToCounter.collectionElementType();

        if (clazz == Post.class) {
            Post post = (Post) returnValue;
            postViews.computeIfAbsent(post.getId(), id -> new AtomicInteger(0)).incrementAndGet();
            System.out.println();
        }
        if (clazz == List.class) {
            if (elementType == Post.class) {
                List<Post> posts = (List<Post>) returnValue;
                posts.forEach(post -> postViews.computeIfAbsent(post.getId(), id -> new AtomicInteger(0)).incrementAndGet());
                System.out.println();
            } else if (elementType == PostFeedResponseDto.class) {
                List<PostFeedResponseDto> posts = (List<PostFeedResponseDto>) returnValue;
                posts.forEach(post -> postViews.computeIfAbsent(post.getId(), id -> new AtomicInteger(0)).incrementAndGet());
                System.out.println();
            }
        }
    }

    public void publishCommentLikesMapToKafka() {
        lock.lock();
        try {
            if (!postViews.isEmpty()) {
                Map<Long, Integer> postViewsToKafka = new HashMap<>();
                postViews.forEach((postId, count) -> postViewsToKafka.put(postId, count.get()));

                userActionKafkaProducer.sendPostViewsMapToKafka(postViewsToKafka);

                postViews.clear();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }
}
