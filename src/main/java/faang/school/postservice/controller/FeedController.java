package faang.school.postservice.controller;

import faang.school.postservice.model.dto.FeedDto;
import faang.school.postservice.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping
@Validated
@Tag(name = "Feed Controller", description = "The controller is used to receive feeds")
public class FeedController {
    private final FeedService feedService;

    @GetMapping("/{id}")
    @Operation(summary = "Get feed by ID", description = "Retrieve feed by its ID.")
    public FeedDto getAlbumById(
            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") Long userId,
            @RequestParam(required = false) Integer startPostId,
            @NotNull @PathVariable Long id) {
        return feedService.getFeed(id, userId, startPostId);
    }
}
