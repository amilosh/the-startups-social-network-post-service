package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.message.UsersFeedUpdateMessage;
import faang.school.postservice.publisher.kafka.publishers.simple.UserIdsToFeedUpdateToKafkaPublisher;
import faang.school.postservice.service.feed.util.EventsPartitioner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
@Service
public class FeedHeaterService {
    private final UserIdsToFeedUpdateToKafkaPublisher userIdsPublisher;
    private final UserServiceClient userServiceClient;
    private final EventsPartitioner partitioner;
    private final Executor usersFeedsUpdatePool;
    private final FeedService feedService;

    public void heatUsersFeeds() {
        List<Long> usersIds = userServiceClient.getAllIds();

        List<UsersFeedUpdateMessage> messages = partitioner.partitionUserIdsAndMapToMessage(usersIds);

        messages.forEach(userIdsPublisher::publish);
    }

    public void updateUsersFeeds(List<Long> usersIds) {
        usersIds.forEach(id -> usersFeedsUpdatePool.execute(() -> feedService.updateUserFeed(id)));
    }
}
