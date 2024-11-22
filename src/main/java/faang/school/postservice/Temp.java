package faang.school.postservice;

import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Component;

@EnableRetry()
@Component
@RequiredArgsConstructor
public class Temp implements CommandLineRunner {

    // TODO remove this file
    private final PostCacheRedisRepository postCacheRedisRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheMapper postCacheMapper;

    @Override
//    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 5000), retryFor = IllegalStateException.class)
    public void run(String... args) throws Exception {
//        try (JedisPool jedisPool = new JedisPool("localhost", 6379)) {
//            try (Jedis jedis = jedisPool.getResource()) {
//                String key = "balance";
//                int decrement = 50;
//                jedis.set(key, "100");
//                jedis.watch(key);
//                int currentBalance = Integer.parseInt(jedis.get(key));
//                Transaction transaction = jedis.multi();
//                System.out.println("Начинаем ждать");
//                Thread.sleep(30000);
//                transaction.set(key, String.valueOf(currentBalance - decrement));
//
//                if (transaction.exec() != null) {
//                    System.out.println("Операция выполнена успешно. Новый баланс: " + (currentBalance - decrement));
//                } else {
//                    System.out.println("Операция не выполнена. Данные были изменены другим клиентом.");
//                    throw new IllegalStateException();
//                }
//            }
//        }
    }
}
