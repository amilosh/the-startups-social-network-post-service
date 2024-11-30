package faang.school.postservice.service.cache;

import faang.school.postservice.dto.post.message.NewPostMessage;
import faang.school.postservice.publisher.kafka.publishers.simple.PartitionSubscribersToKafkaPublisher;
import faang.school.postservice.repository.cache.CommentCacheRepository;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.repository.cache.UserCacheRepository;
import faang.school.postservice.service.feed.util.EventsPartitioner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CacheUpdateService {
    private final PartitionSubscribersToKafkaPublisher partitionPublisher;
    private final CommentCacheRepository commentCacheRepository;
    private final PostCacheRepository postCacheRepository;
    private final EventsPartitioner partitioner;
    private final UserCacheRepository userCacheRepository;

    public void partitionSubscribersAndPublish(NewPostMessage newPostMessage) {
        List<NewPostMessage> messages = partitioner.partitionSubscribersAndMapToMessage(newPostMessage.getPostId(),
                newPostMessage.getAuthorId(), newPostMessage.getCreatedAtTimestamp(), newPostMessage.getFollowersIds());
        messages.forEach(partitionPublisher::publish);
    }

    public void usersFeedUpdate(Long postId, Long timestamp, List<Long> usersId) {
        usersId.forEach(id -> userCacheRepository.userFeedUpdate(id, postId, timestamp));
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

    public void commentLikesUpdate(List<String> commentsLikesCountersKeys) {
        commentsLikesCountersKeys.forEach(commentCacheRepository::assignLikesByCounter);
    }
}
