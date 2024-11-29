package faang.school.postservice.config.api;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spelling")
public class SpellingConfig {

    private String key;
    private String endpoint;
    private String host;
    private String content;

}
