package faang.school.postservice.model.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash(value = "Users")
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto implements Serializable {
    @Positive
    private Long id;
    @NotBlank(message = "Name can not be null or empty")
    @Max(100)
    private String name;
    @NotBlank(message = "Username can not be null or empty")
    @Max(64)
    private String username;
    @NotBlank(message = "E-mail can not be null or empty")
    @Email
    @Max(64)
    private String email;
}