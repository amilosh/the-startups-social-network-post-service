package faang.school.postservice.service.feed.util;

import faang.school.postservice.dto.post.message.NewPostMessage;
import faang.school.postservice.dto.post.message.UsersFeedUpdateMessage;
import faang.school.postservice.dto.post.message.counter.CommentCountersKeysMessage;
import faang.school.postservice.dto.post.message.counter.CommentLikeCounterKeysMessage;
import faang.school.postservice.dto.post.message.counter.PostLikeCountersKeysMessage;
import faang.school.postservice.dto.post.message.counter.PostViewCountersKeysMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class EventsPartitioner {
    private final ListPartitioner partitioner;

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
        List<List<Long>> usersIdByGroup = partitioner.exec(usersId, followerPartitionsLimit);

        return usersIdByGroup.stream()
                .map(group -> NewPostMessage.builder()
                        .postId(postId)
                        .authorId(authorId)
                        .createdAtTimestamp(timestamp)
                        .followersIds(group)
                        .build())
                .toList();
    }

    public List<PostViewCountersKeysMessage> partitionViewCounterKeysAndMapToMessage(List<String> keys) {
        List<List<String>> countersGroups = partitioner.exec(keys, viewCountersPartitionLimit);

        return countersGroups.stream()
                .map(PostViewCountersKeysMessage::new)
                .toList();
    }

    public List<PostLikeCountersKeysMessage> partitionLikeCounterKeysAndMapToMessage(List<String> keys) {
        List<List<String>> countersGroups = partitioner.exec(keys, likeCountersPartitionLimit);

        return countersGroups.stream()
                .map(PostLikeCountersKeysMessage::new)
                .toList();
    }

    public List<CommentCountersKeysMessage> partitionCommentCounterKeysAndMapToMessage(List<String> keys) {
        List<List<String>> counterGroups = partitioner.exec(keys, commentCountersPartitionLimit);

        return counterGroups.stream()
                .map(CommentCountersKeysMessage::new)
                .toList();
    }

    public List<CommentLikeCounterKeysMessage> partitionCommentLikeCounterKeysAndMapToMessage(List<String> keys) {
        List<List<String>> countersGroups = partitioner.exec(keys, commentLikesPartitionLimit);

        return countersGroups.stream()
                .map(CommentLikeCounterKeysMessage::new)
                .toList();
    }

    public List<UsersFeedUpdateMessage> partitionUserIdsAndMapToMessage(List<Long> ids) {
        List<List<Long>> idsGroups = partitioner.exec(ids, usersFeedUpdatePartitionLimit);

        return idsGroups.stream()
                .map(UsersFeedUpdateMessage::new)
                .toList();
    }
}
