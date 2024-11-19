package faang.school.postservice.config.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    // TODO asdfsdfg

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

//    @Value(value = "${spring.kafka.topics.comment}")
//    private String testTopic;

//    @Bean
//    public KafkaAdmin kafkaAdmin() {
//        Map<String, Object> configs = new HashMap<>();
//        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
//        return new KafkaAdmin(configs);
//    }

//    @Bean
//    public NewTopic testTopic() {
//        return new NewTopic(testTopic, 1, (short) 1);
//    }
}
