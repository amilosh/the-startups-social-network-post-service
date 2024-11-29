package faang.school.postservice.publisher.kafka;

import faang.school.postservice.model.event.kafka.CommentEventKafka;
import faang.school.postservice.redis.service.PostCacheService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class KafkaCommentProducerConsumerTest {
    // TODO make tests

//    @Autowired
//    MockMvc mockMvc;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockBean
    private PostCacheService postCacheService;

    @Container
    private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13.3")
            .withDatabaseName("testdb")
            .withUsername("admin")
            .withPassword("admin")
            .withInitScript("schema_for_feed-controller.sql");

    @Container
    private static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.0-alpine")
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void overrideSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
        registry.add("spring.liquibase.enabled", () -> false);
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
        registry.add("feed-posts-per-request.size", () -> 2);
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Container
    private static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"))
            .withExposedPorts(9093);

    @Test
    public void testProducerAndConsumer() throws InterruptedException {
        String topic = "comment_topic";
        String key = "test-key";
        CommentEventKafka testEvent =
                new CommentEventKafka(1L, 2L, 3L, "Test comment", LocalDateTime.now());

        kafkaTemplate.send(topic, key, testEvent);

        Thread.sleep(2000);

        verify(postCacheService).updatePostComments(testEvent);
    }
}
