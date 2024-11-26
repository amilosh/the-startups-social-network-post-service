package faang.school.postservice;


import faang.school.postservice.repository.NewsFeedRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
@Slf4j
public class TestRedis {

    @Autowired
    private NewsFeedRedisRepository newsFeedRedisRepository;

    private final Long KEY = 2L;

    private int counter = 0;

    @Test
    public void test() throws InterruptedException {


        Thread one = new Thread(() -> newsFeedRedisRepository.addPostId(100L, KEY, counter++));
        one.start();
        one.join();


        List<Long> postIds = newsFeedRedisRepository.getPostIdsFirstBatch(KEY);
        System.out.println(postIds);
    }
}
