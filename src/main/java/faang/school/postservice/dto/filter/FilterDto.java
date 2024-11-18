package faang.school.postservice.dto.filter;

import faang.school.postservice.sort.PostField;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FilterDto {
    @NotNull
    private Boolean author;
    private Boolean published;
    private Boolean deleted;
    @NotNull
    private PostField postField;
}
