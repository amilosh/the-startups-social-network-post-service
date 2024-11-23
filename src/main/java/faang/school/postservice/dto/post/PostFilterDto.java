package faang.school.postservice.dto.post;

import lombok.Data;

@Data
public class PostFilterDto {
    private Long id;
    private boolean published;
    private String type;

}
