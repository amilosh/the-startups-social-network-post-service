package faang.school.postservice.listener.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.message.CommentPostMessage;
import faang.school.postservice.dto.post.message.LikeCommentMessage;
import faang.school.postservice.dto.post.message.LikePostMessage;
import faang.school.postservice.dto.post.message.NewPostMessage;
import faang.school.postservice.dto.post.message.UsersFeedUpdateMessage;
import faang.school.postservice.dto.post.message.ViewPostMessage;
import faang.school.postservice.dto.post.message.counter.CommentCountersKeysMessage;
import faang.school.postservice.dto.post.message.counter.CommentLikeCounterKeysMessage;
import faang.school.postservice.dto.post.message.counter.PostLikeCountersKeysMessage;
import faang.school.postservice.dto.post.message.counter.PostViewCountersKeysMessage;
import faang.school.postservice.service.cache.CacheUpdateService;
import faang.school.postservice.service.feed.FeedHeaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaListeners {
    private final CacheUpdateService cacheUpdateService;
    private final FeedHeaterService feedHeaterService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topic.post.new}", groupId = "${spring.kafka.consumer.group-id}")
    public void newPost(String message) {
        NewPostMessage newPostMessage = readMessage(message, NewPostMessage.class);
        cacheUpdateService.partitionSubscribersAndPublish(newPostMessage);
    }

    @KafkaListener(topics = "${spring.kafka.topic.post.update_feeds}", groupId = "${spring.kafka.consumer.group-id}")
    public void updateSubscribersFeeds(String message) {
        NewPostMessage newPostMessage = readMessage(message, NewPostMessage.class);
        cacheUpdateService.usersFeedUpdate(newPostMessage.getPostId(), newPostMessage.getCreatedAtTimestamp(),
                newPostMessage.getFollowersIds());
    }

    @KafkaListener(topics = "${spring.kafka.topic.post.view}", groupId = "${spring.kafka.consumer.group-id}")
    public void viewPost(String message) {
        ViewPostMessage viewPostMessage = readMessage(message, ViewPostMessage.class);
        cacheUpdateService.postsViewsIncrByIds(viewPostMessage.getPostsIds());
    }

    @KafkaListener(topics = "${spring.kafka.topic.post.update_views}", groupId = "${spring.kafka.consumer.group-id}")
    public void postViewsUpdate(String message) {
        PostViewCountersKeysMessage viewsMessage = readMessage(message, PostViewCountersKeysMessage.class);
        cacheUpdateService.postViewsUpdate(viewsMessage.getViewCountersKeys());
    }

    @KafkaListener(topics = "${spring.kafka.topic.post.like}", groupId = "${spring.kafka.consumer.group-id}")
    public void likePost(String message) {
        LikePostMessage likePostMessage = readMessage(message, LikePostMessage.class);
        cacheUpdateService.postLikesIncrById(likePostMessage.getPostId());
    }

    @KafkaListener(topics = "${spring.kafka.topic.post.update_likes}", groupId = "${spring.kafka.consumer.group-id}")
    public void postLikesUpdate(String message) {
        PostLikeCountersKeysMessage likesMessage = readMessage(message, PostLikeCountersKeysMessage.class);
        cacheUpdateService.postLikesUpdate(likesMessage.getLikeCountersKeys());
    }

    @KafkaListener(topics = "${spring.kafka.topic.post.comment}", groupId = "${spring.kafka.consumer.group-id}")
    public void commentPost(String message) {
        CommentPostMessage commentPostMessage = readMessage(message, CommentPostMessage.class);
        cacheUpdateService.commentsCounterIncrById(commentPostMessage.getPostId());
    }

    @KafkaListener(topics = "${spring.kafka.topic.post.update_comments}", groupId = "${spring.kafka.consumer.group-id}")
    public void commentsUpdate(String message) {
        CommentCountersKeysMessage likesMessage = readMessage(message, CommentCountersKeysMessage.class);
        cacheUpdateService.commentsUpdate(likesMessage.getCommentCountersKeys());
    }

    @KafkaListener(topics = "${spring.kafka.topic.post.like_post_comment}", groupId = "${spring.kafka.consumer.group-id}")
    public void likeComment(String message) {
        LikeCommentMessage likeCommentMessage = readMessage(message, LikeCommentMessage.class);
        cacheUpdateService.commentLikesIncrById(likeCommentMessage.getCommentId());
    }

    @KafkaListener(topics = "${spring.kafka.topic.post.update_post_comments_likes}", groupId = "${spring.kafka.consumer.group-id}")
    public void commentLikesUpdate(String message) {
        CommentLikeCounterKeysMessage commentLikesMessage = readMessage(message,
                CommentLikeCounterKeysMessage.class);
        cacheUpdateService.commentsLikesUpdate(commentLikesMessage.getCommentLikeCounterKeys());
    }

    @KafkaListener(topics = "${spring.kafka.topic.user.feed_update}", groupId = "${spring.kafka.consumer.group-id}")
    public void usersFeedUpdate(String message) {
        UsersFeedUpdateMessage usersFeedUpdateMessage = readMessage(message, UsersFeedUpdateMessage.class);
        feedHeaterService.updateUsersFeeds(usersFeedUpdateMessage.getUserIds());
    }

    private <T> T readMessage(String message, Class<T> clazz) {
        try {
            return objectMapper.readValue(message, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
