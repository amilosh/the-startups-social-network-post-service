package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.message.UsersFeedUpdateMessage;
import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.publisher.kafka.publishers.simple.UserIdsToFeedUpdateToKafkaPublisher;
import faang.school.postservice.repository.cache.CommentCacheRepository;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.repository.cache.UserCacheRepository;
import faang.school.postservice.service.feed.util.EventsPartitioner;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
@Service
public class FeedHeaterService {
    private final UserIdsToFeedUpdateToKafkaPublisher userIdsPublisher;
    private final CommentCacheRepository commentCacheRepository;
    private final PostCacheRepository postCacheRepository;
    private final UserCacheRepository userCacheRepository;
    private final UserServiceClient userServiceClient;
    private final EventsPartitioner partitioner;
    private final Executor usersFeedsUpdatePool;
    private final PostService postService;

    @Value("${app.post.cache.news_feed.user_feed_size}")
    private long userFeedSize;

    public void heatUsersFeeds() {
        List<Long> usersIds = userServiceClient.getAllIds();

        List<UsersFeedUpdateMessage> messages = partitioner.partitionUserIdsAndMapToMessage(usersIds);

        messages.forEach(userIdsPublisher::publish);
    }

    public void updateUsersFeeds(List<Long> usersIds) {
        usersIds.forEach(id -> usersFeedsUpdatePool.execute(() -> updateUserFeed(id)));
    }

    public void updateUserFeed(long userId) {
        List<PostCacheDto> postDtoList = postService.getSetOfPosts(userId, 0L, userFeedSize);

        userCacheRepository.mapAndSavePostIdsToFeed(userId, postDtoList);

        postDtoList = postCacheRepository.filterPostsOnWithoutCache(postDtoList);
        postCacheRepository.saveAll(postDtoList);

        postDtoList.forEach(postCache -> commentCacheRepository.saveAll(postCache.getComments()));

        Set<Long> userIds = postCacheRepository.mapPostDtoListToAuthorsIds(postDtoList);

        List<Long> filteredUserIds = userCacheRepository.filterUserIdsOnWithoutCache(userIds);

        List<UserDto> userDtoList = userServiceClient.getUsersByIds(filteredUserIds);
        userCacheRepository.saveAll(userDtoList);
    }
}
