package faang.school.postservice.dto;

import faang.school.postservice.model.ProjectStatus;
import faang.school.postservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectDto {
    private Long id;

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    @Size(max = 128, message = "Name must not exceed 128 characters")
    private String name;

    @Size(max = 4096, message = "Description must not exceed 4096 characters")
    private String description;

    @NotNull(message = "OwnerId must not be null")
    private Long ownerId;
    private Long parentProjectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    @NotNull(message = "Visibility must not be null")
    private ProjectVisibility visibility;
    private String coverImageId;
    private List<Long> teamIds;
}