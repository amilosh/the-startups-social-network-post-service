package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.CacheableNewsFeed;
import faang.school.postservice.cache.model.CacheablePost;
import faang.school.postservice.cache.model.CacheableUser;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.kafka.event.heater.HeaterNewsFeedEvent;
import faang.school.postservice.kafka.event.heater.HeaterPostsEvent;
import faang.school.postservice.kafka.event.heater.HeaterUsersEvent;
import faang.school.postservice.kafka.producer.KafkaProducer;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.util.ListSplitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsFeedHeater {
    private final UserServiceClient userServiceClient;
    private final KafkaProducer kafkaProducer;
    private final PostService postService;
    private final CacheablePostService cacheablePostService;
    private final NewsFeedService newsFeedService;

    @Value("${news-feed.heater.batch-size}")
    private int batchSize;
    @Value("${spring.kafka.topic.heater.users}")
    private String heaterUsersTopic;
    @Value("${spring.kafka.topic.heater.news-feeds}")
    private String heaterNewsFeedsTopic;
    @Value("${spring.kafka.topic.heater.posts}")
    private String heaterPostsTopic;

    public void heat() {
        List<CacheableUser> cacheableUsers = userServiceClient.getActiveCacheableUsers();
        splitAndSendUsersEvents(cacheableUsers);

        List<CacheableNewsFeed> newsFeeds = newsFeedService.getNewsFeedsForUsers(cacheableUsers);
        splitAndSendNewsFeedsEvents(newsFeeds);

        List<Long> postIds = getUniquePostIds(newsFeeds);
        splitAndSendPostsEvents(postIds);
    }

    public void saveAllPosts(List<Long> postIds) {
        List<CacheablePost> cacheablePosts = postService.findAllByIdsWithLikes(postIds);
        cacheablePostService.setCommentsFromDB(cacheablePosts);
        cacheablePostService.saveAll(cacheablePosts);
    }

    private List<Long> getUniquePostIds(List<CacheableNewsFeed> newsFeeds) {
        return newsFeeds.stream()
                .flatMap(newsFeed -> newsFeed.getPostIds().stream())
                .distinct()
                .toList();
    }

    private void splitAndSendNewsFeedsEvents(List<CacheableNewsFeed> newsFeeds) {
        List<List<CacheableNewsFeed>> splitNewsFeeds = ListSplitter.split(newsFeeds, batchSize);
        splitNewsFeeds.forEach(list -> kafkaProducer.send(heaterNewsFeedsTopic, new HeaterNewsFeedEvent(list)));
    }

    private void splitAndSendUsersEvents(List<CacheableUser> cacheableUsers) {
        List<List<CacheableUser>> splitUsers = ListSplitter.split(cacheableUsers, batchSize);
        splitUsers.forEach(list -> kafkaProducer.send(heaterUsersTopic, new HeaterUsersEvent(list)));
    }

    private void splitAndSendPostsEvents(List<Long> postIds) {
        List<List<Long>> splitIds = ListSplitter.split(postIds, batchSize);
        splitIds.forEach(list -> kafkaProducer.send(heaterPostsTopic, new HeaterPostsEvent(list)));
    }
}
