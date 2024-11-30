package faang.school.postservice.service.feed.util;

import faang.school.postservice.dto.post.message.NewPostMessage;
import faang.school.postservice.dto.post.message.UsersFeedUpdateMessage;
import faang.school.postservice.dto.post.message.counter.CommentCountersKeysMessage;
import faang.school.postservice.dto.post.message.counter.CommentLikeCounterKeysMessage;
import faang.school.postservice.dto.post.message.counter.PostLikeCountersKeysMessage;
import faang.school.postservice.dto.post.message.counter.PostViewCountersKeysMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@Component
public class EventsPartitioner {
    @Value("${app.post.feed.update.followers_partitions_limit}")
    private int followerPartitionsLimit;

    @Value("${app.post.feed.update.view_counter_partition_limit}")
    private int viewCountersPartitionLimit;

    @Value("${app.post.feed.update.like_counter_partition_limit}")
    private int likeCountersPartitionLimit;

    @Value("${app.post.feed.update.comment_counter_partition_limit}")
    private int commentCountersPartitionLimit;

    @Value("${app.post.feed.update.post_comment_likes_partition_limit}")
    private int commentLikesPartitionLimit;

    @Value("${app.post.feed.update.users_feed_update_partition_limit}")
    private int usersFeedUpdatePartitionLimit;

    public List<NewPostMessage> partitionSubscribersAndMapToMessage(Long postId, Long authorId, Long timestamp,
                                                                    List<Long> usersId) {
        return partitionAndMap(usersId, followerPartitionsLimit, group ->
            NewPostMessage.builder()
                    .postId(postId)
                    .authorId(authorId)
                    .createdAtTimestamp(timestamp)
                    .followersIds(group)
                    .build());
    }

    public List<PostViewCountersKeysMessage> partitionViewCounterKeysAndMapToMessage(List<String> keys) {
        return partitionAndMap(keys, viewCountersPartitionLimit, PostViewCountersKeysMessage::new);
    }

    public List<PostLikeCountersKeysMessage> partitionLikeCounterKeysAndMapToMessage(List<String> keys) {
        return partitionAndMap(keys, likeCountersPartitionLimit, PostLikeCountersKeysMessage::new);
    }

    public List<CommentCountersKeysMessage> partitionCommentCounterKeysAndMapToMessage(List<String> keys) {
        return partitionAndMap(keys, commentCountersPartitionLimit, CommentCountersKeysMessage::new);
    }

    public List<CommentLikeCounterKeysMessage> partitionCommentLikeCounterKeysAndMapToMessage(List<String> keys) {
        return partitionAndMap(keys, commentLikesPartitionLimit, CommentLikeCounterKeysMessage::new);
    }

    public List<UsersFeedUpdateMessage> partitionUserIdsAndMapToMessage(List<Long> ids) {
        return partitionAndMap(ids, usersFeedUpdatePartitionLimit, UsersFeedUpdateMessage::new);
    }

    private <T, K> List<T> partitionAndMap(List<K> list, int size, Function<List<K>, T> function) {
        List<List<K>> lists = ListUtils.partition(list, size);
        return lists.stream()
                .map(function)
                .toList();
    }
}
