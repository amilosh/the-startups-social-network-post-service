package faang.school.postservice.service;

import faang.school.postservice.dto.news_feed.FeedPostDto;
import faang.school.postservice.repository.NewsFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NewsFeedService {
    private final NewsFeedRepository newsFeedRepository;

    public void allocateToFeeds(Long postId, Long createdAt, List<Long> userIds) {
        userIds.stream()
                .map(String::valueOf)
                .forEach(followerId -> newsFeedRepository.addPost(postId.toString(), followerId, createdAt));
    }

//    public List<FeedPostDto> getFeedPost(Long lastPostId) {
//        if (Objects.isNull(lastPostId)) {
//
//        }
//    }
}
