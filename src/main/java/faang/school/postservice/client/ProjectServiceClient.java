package faang.school.postservice.client;

import faang.school.postservice.dto.project.ProjectDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "project-service", url = "${project-service.host}:${project-service.port}")
public interface ProjectServiceClient {

    @GetMapping("/api/v1/projects/project/{projectId}")
    ProjectDto getProjectById(@PathVariable Long projectId, @RequestParam Long currentUserId);

    @GetMapping("/api/v1/projects/all-projects")
    List<ProjectDto> getAllProjects(@RequestParam Long currentUserId);
}