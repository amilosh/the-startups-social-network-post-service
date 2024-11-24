package faang.school.postservice;



import faang.school.postservice.repository.NewsFeedRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Slf4j
public class TestRedis {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private NewsFeedRepository newsFeedRepository;

    private static final String KEY = "myKey";

    private long counter = 0;

    @Test
    public void test() throws InterruptedException {
        redisTemplate.opsForZSet().removeRange(KEY, 0, -1);
        LongStream.range(0, 10)
                .mapToObj(String::valueOf)
                        .forEach(postId -> redisTemplate.opsForZSet().add(KEY, postId, counter++));

        List<String> values = newsFeedRepository.getFeed(KEY);
        values.forEach(val -> System.out.print(val + " "));
        System.out.println();
        assertEquals(10, values.size());

        Thread one = new Thread(() -> newsFeedRepository.addPost(String.valueOf(100), KEY, counter++));
        Thread two = new Thread(() -> newsFeedRepository.addPost(String.valueOf(200), KEY, counter++));

        one.start();
        two.start();

        one.join();
        two.join();

        List<String> newValues = newsFeedRepository.getFeed(KEY);
        newValues.forEach(val -> System.out.print(val + " "));
        System.out.println();
        assertEquals(10, values.size());
    }
}
