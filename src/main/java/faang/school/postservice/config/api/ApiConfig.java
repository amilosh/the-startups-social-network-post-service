package faang.school.postservice.config.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiConfig {

//    @Value("${api.key}")
    private String apiKey;

//    @Value("${api.endpoint}")
    private String apiEndpoint;

}
