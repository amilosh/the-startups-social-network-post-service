package faang.school.postservice.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class KafkaPublisherAsyncThreadPoolConfig {
    @Value("${app.async-config.redis-publisher-async-pool.core_pool_size}")
    private int corePoolSize;

    @Value("${app.async-config.redis-publisher-async-pool.max_pool_size}")
    private int maxPoolSize;

    @Bean
    public Executor kafkaPublisherExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        return executor;
    }
}
