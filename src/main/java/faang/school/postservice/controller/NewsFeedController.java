package faang.school.postservice.controller;

import faang.school.postservice.dto.post.FeedPost;
import faang.school.postservice.mapper.FeedPostMapper;
import faang.school.postservice.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class NewsFeedController {
    private final NewsFeedService newsFeedService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FeedPost> getFeed(@RequestParam(name = "last-post-id", required = false) Long lastPostId,
                                  @RequestHeader("x-user-id") Long userId) {
        return newsFeedService.getFeedBatch(userId, lastPostId);
    }


    @PutMapping("/heat")
    @ResponseStatus(HttpStatus.OK)
    public void heat() {
        newsFeedService.startHeat();
    }
}