package faang.school.postservice.controller.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.service.feed.FeedHeaterService;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/feed")
@RequiredArgsConstructor
@RestController
public class NewsFeedController {
    private final FeedHeaterService feedHeaterService;
    private final FeedService feedService;
    private final UserContext userContext;

    @GetMapping
    public List<PostCacheDto> getSetOfPosts(@RequestParam(name = "offset") Long offset,
                                            @RequestParam(name = "limit") Long limit) {

        return feedService.getSetOfPosts(userContext.getUserId(), offset, limit);
    }

    @GetMapping("/heat")
    public void heatUsersFeed() {
        feedHeaterService.heatUsersFeeds();
    }
}
