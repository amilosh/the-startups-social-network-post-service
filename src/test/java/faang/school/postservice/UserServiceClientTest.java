package faang.school.postservice;

import faang.school.postservice.service.NewsFeedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceClientTest {

    @Autowired
    private NewsFeedService newsFeedService;

    @Test
    public void test() throws InterruptedException {
        newsFeedService.startHeat();

        Thread.sleep(10000);
    }
}
