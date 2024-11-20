package faang.school.postservice.service.impl;

import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.repository.FeedCacheRepository;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final FeedCacheRepository cacheRepository;
    @Override
    public void distributePostsToUsersFeeds(PostPublishedEvent event) {

    }
}
