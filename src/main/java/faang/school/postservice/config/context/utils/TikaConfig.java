package faang.school.postservice.config.context.utils;

import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TikaConfig {

    @Bean
    public Tika baseTika() {
        return new Tika();
    }
}
