package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostFeedResponseDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/feed")
@RestController
public class FeedController {
    private final UserContext userContext;
    private final FeedService feedService;

    @GetMapping
    public List<PostFeedResponseDto> loadNextPosts(@RequestParam(required = false) Long postId) {
        long userId = userContext.getUserId();
        feedService.getFeed(userId, postId);
        return new ArrayList<>();
    }

    @PostMapping("/{userId}/{postId}")
    public void addFeed(@PathVariable Long userId, @PathVariable Long postId) {
        LocalDateTime publishedAt = LocalDateTime.now();
        feedService.addFeed(userId, postId, publishedAt);
    }
}
