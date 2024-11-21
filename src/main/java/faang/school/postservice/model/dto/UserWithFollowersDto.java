package faang.school.postservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithFollowersDto {
    private Long id;
    private String username;
    private String fileId;
    private String smallFileId;
    private LocalDateTime createdAt;
    private List<Long> followerIds;
}
