package faang.school.postservice.service;

import faang.school.postservice.repository.NewsFeedRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsFeedService {
    private final NewsFeedRedisRepository newsFeedRedisRepository;


    public void allocateToFeeds(Long postId, Long createdAt, List<Long> userIds) {
        userIds.forEach(userId -> newsFeedRedisRepository.addPostId(postId, userId, createdAt));

    }
}
