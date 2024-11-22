package faang.school.postservice.dto.post;

import faang.school.postservice.dto.like.LikeRequestDto;
import faang.school.postservice.model.Like;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostFilterDto {
    private Long id;
    private boolean published;
    private String type;

}
