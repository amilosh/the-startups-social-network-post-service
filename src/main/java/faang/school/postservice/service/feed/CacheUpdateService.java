package faang.school.postservice.service.feed;

import faang.school.postservice.dto.post.message.NewPostMessage;
import faang.school.postservice.publisher.kafka.publishers.simple.PartitionSubscribersToKafkaPublisher;
import faang.school.postservice.repository.cache.CommentCacheRepository;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.repository.cache.ZSetRepository;
import faang.school.postservice.service.feed.util.EventsPartitioner;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CacheUpdateService {
    private final PartitionSubscribersToKafkaPublisher partitionPublisher;
    private final CommentCacheRepository commentCacheRepository;
    private final PostCacheRepository postCacheRepository;
    private final EventsPartitioner partitioner;
    private final ZSetRepository zSetRepository;

    @Value("${app.post.cache.news_feed.prefix.post_id}")
    private String postIdPrefix;

    @Value("${app.post.cache.news_feed.prefix.feed_user_id}")
    private String feedUserIdPrefix;

    @Value("${app.post.cache.news_feed.user_feed_size}")
    private long numberOfTopInCache;

    public void partitionSubscribersAndPublish(NewPostMessage newPostMessage) {
        List<NewPostMessage> messages = partitioner.partitionSubscribersAndMapToMessage(newPostMessage.getPostId(),
                newPostMessage.getAuthorId(), newPostMessage.getCreatedAtTimestamp(), newPostMessage.getFollowersIds());
        messages.forEach(partitionPublisher::publish);
    }

    public void usersFeedUpdate(Long postId, Long timestamp, List<Long> usersId) {
        long limit = (numberOfTopInCache + 1) * -1;
        String postIdValue = postIdBuild(postId);
        usersId.forEach(id -> {
            String userIdKey = feedUserIdBuild(id);
            zSetRepository.setAndRemoveRange(userIdKey, postIdValue, timestamp, limit);
        });
    }

    public void postsViewsIncrByIds(List<Long> postsId) {
        postsId.forEach(postCacheRepository::incrementPostViews);
    }

    public void postViewsUpdate(List<String> viewCountersKeys) {
        viewCountersKeys.forEach(postCacheRepository::assignViewsByCounter);
    }

    public void postLikesIncrById(Long postId) {
        postCacheRepository.incrementPostLikes(postId);
    }

    public void postLikesUpdate(List<String> likeCountersKeys) {
        likeCountersKeys.forEach(postCacheRepository::assignLikesByCounter);
    }

    public void commentsCounterIncrById(Long postId) {
        postCacheRepository.incrementComments(postId);
    }

    public void commentsUpdate(List<String> commentCountersKeys) {
        commentCountersKeys.forEach(postCacheRepository::assignCommentsByCounter);
    }

    public void commentLikesIncrById(Long commentId) {
        commentCacheRepository.incrementCommentLikes(commentId);
    }

    public void commentsLikesUpdate(List<String> commentsLikesCountersKeys) {
        commentsLikesCountersKeys.forEach(commentCacheRepository::assignLikesByCounter);
    }

    private String postIdBuild(long id) {
        return postIdPrefix + id;
    }

    private String feedUserIdBuild(long id) {
        return feedUserIdPrefix + id;
    }
}
