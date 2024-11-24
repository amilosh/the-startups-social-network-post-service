package faang.school.postservice.controller;

import faang.school.postservice.dto.news_feed.FeedPostDto;
import faang.school.postservice.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class NewsFeedController {
    private final NewsFeedService newsFeedService;

//    @GetMapping
//    @ResponseStatus(HttpStatus.OK)
//    public List<FeedPostDto> getFeedPost(@RequestParam(value = "last-post", required = false) Long lastPostId) {
//        List<FeedPostDto> feedPostList = newsFeedService.getPostBatch(lastPostId);
//
//
//
//    }
}
