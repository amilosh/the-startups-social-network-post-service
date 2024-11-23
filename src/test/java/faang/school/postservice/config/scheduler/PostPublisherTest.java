package faang.school.postservice.config.scheduler;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;


class PostPublisherTest {

    PostPublisher postPublisher = mock(PostPublisher.class);

    @Test
    void testAsyncMethodWithMockito() {

        postPublisher.checkPostToPublish();

        verify(postPublisher, timeout(1000)).checkPostToPublish();
    }

    @Test
    void testAsyncVoidMethod() throws InterruptedException {
        postPublisher.checkPostToPublish();

        TimeUnit.SECONDS.sleep(2);
    }
}