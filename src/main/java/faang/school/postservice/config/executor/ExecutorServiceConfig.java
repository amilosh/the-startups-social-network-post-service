package faang.school.postservice.config.executor;

import faang.school.postservice.config.properties.ThreadPoolProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class ExecutorServiceConfig {

    private final ThreadPoolProperties properties;

    @Bean
    public ExecutorService cachedExecutor() {
        return Executors.newCachedThreadPool();
    }

    @Bean
    public ExecutorService customExecutor() {
        return new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaximumPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(properties.getCapacity())
        );
    }
}
