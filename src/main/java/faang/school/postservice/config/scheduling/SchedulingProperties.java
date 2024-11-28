package faang.school.postservice.config.scheduling;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "scheduling")
@Data
public class SchedulingProperties {

    private String cron;

}
