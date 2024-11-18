package faang.school.postservice.service.feed;

import faang.school.postservice.annotations.publisher.PublishEvent;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.serializable.CommentCacheDto;
import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.cache.CommentCacheRepository;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.repository.cache.UserCacheRepository;
import faang.school.postservice.repository.cache.ZSetRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static faang.school.postservice.enums.publisher.PublisherType.VIEW_POST;
import static java.lang.Boolean.FALSE;

@RequiredArgsConstructor
@Service
public class FeedService {
    private static final Object MOCK = new Object();

    private final CommentCacheRepository commentCacheRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostCacheRepository postCacheRepository;
    private final UserCacheRepository userCacheRepository;
    private final UserServiceClient userServiceClient;
    private final ZSetRepository zSetRepository;
    private final Executor usersFeedsUpdatePool;
    private final PostService postService;

    @Value("${app.post.cache.news_feed.prefix.feed_user_id}")
    private String feedUserIdPrefix;

    @Value("${app.post.cache.news_feed.prefix.post_id}")
    private String postIdPrefix;

    @Value("${app.post.cache.news_feed.prefix.user_id}")
    private String userIdPrefix;

    @Value("${app.post.cache.news_feed.user_feed_size}")
    private long userFeedSize;

    private final Map<String, Object> feedIsUpdating = new ConcurrentHashMap<>();

    @PublishEvent(type = VIEW_POST)
    public List<PostCacheDto> getSetOfPosts(long userId, long offset, long limit) {
        String feedUserKey = feedUserKey(userId);
        Set<String> postIds = zSetRepository.getValuesInRange(feedUserKey, offset, limit);

        if (postIds.isEmpty()) {
            List<PostCacheDto> setOfPosts = postService.getSetOfPosts(userId, offset, limit);
            List<PostCacheDto> postDtoList = enrichAuthors(setOfPosts);

            if (!feedIsUpdating.containsKey(feedUserKey)) {
                feedIsUpdating.put(feedUserKey, MOCK);
                usersFeedsUpdatePool.execute(() -> updateUserFeed(userId));
            }
            return postDtoList;
        }
        return findPostsInCache(postIds);
    }

    public void updateUserFeed(long id) {
        String feedUserKey = feedUserKey(id);
        feedIsUpdating.put(feedUserKey, MOCK);

        List<PostCacheDto> postDtoList = postService.getSetOfPosts(id, 0L, userFeedSize);

        Set<ZSetOperations.TypedTuple<String>> tuples = postDtoList.stream()
                .map(post -> new DefaultTypedTuple<>(postIdKey(post.getId()), (double) getTimestamp(post.getPublishedAt())))
                .collect(Collectors.toSet());
        zSetRepository.saveTuplesByKey(feedUserKey, tuples);

        updatePostsCommentsAuthorInCache(postDtoList);

        feedIsUpdating.remove(feedUserKey);
    }

    private List<PostCacheDto> findPostsInCache(Set<String> postIds) {
        List<PostCacheDto> postDtoList = postCacheRepository.findAll(postIds);
        setAuthors(postDtoList);

        return postDtoList;
    }

    private void setAuthors(List<PostCacheDto> postDtoList) {
        postDtoList.forEach(post -> {
            setPostAuthor(post);
            setCommentAuthors(post);
        });
    }

    private void setPostAuthor(PostCacheDto postDto) {
        postDto.setAuthorDto(userCacheRepository.findById(postDto.getAuthorId()).orElseGet(() ->
                findSaveGetUserDto(postDto.getAuthorId())));
    }

    private void setCommentAuthors(PostCacheDto postDto) {
        List<CommentCacheDto> commentDtoList = commentCacheRepository.findAllByPostId(postDto.getId());

        commentDtoList.forEach(comment ->
                comment.setAuthorDto(userCacheRepository.findById(comment.getAuthorId()).orElseGet(() ->
                        findSaveGetUserDto(comment.getAuthorId()))));

        postDto.setComments(commentDtoList);
    }

    private UserDto findSaveGetUserDto(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        userCacheRepository.save(userDto);
        return userDto;
    }

    private List<PostCacheDto> enrichAuthors(List<PostCacheDto> postDtoList) {
        Set<String> postIds = postDtoList.stream()
                .map(post -> postIdKey(post.getId()))
                .collect(Collectors.toSet());

        updatePostsCommentsAuthorInCache(postDtoList);

        return findPostsInCache(postIds);
    }

    private void updatePostsCommentsAuthorInCache(List<PostCacheDto> postDtoList) {
        postDtoList = filterPostsOnWithoutCache(postDtoList);
        postCacheRepository.saveAll(postDtoList);

        postDtoList.forEach(postCache -> commentCacheRepository.saveAll(postCache.getComments()));

        Set<Long> userIds = new HashSet<>();
        postDtoList.forEach(postDto -> {
            userIds.add(postDto.getAuthorId());
            postDto.getComments().forEach(comment -> userIds.add(comment.getAuthorId()));
        });

        List<Long> filteredUserIds = filterUserIdsOnWithoutCache(userIds);

        List<UserDto> userDtoList = userServiceClient.getUsersByIds(filteredUserIds);
        userCacheRepository.saveAll(userDtoList);
    }

    private List<PostCacheDto> filterPostsOnWithoutCache(List<PostCacheDto> postDtoList) {
        return postDtoList.stream()
                .filter(post -> FALSE.equals(redisTemplate.hasKey(postIdKey(post.getId()))))
                .toList();
    }

    private List<Long> filterUserIdsOnWithoutCache(Set<Long> userIds) {
        return userIds.stream()
                .filter(userId -> FALSE.equals(redisTemplate.hasKey(userIdKey(userId))))
                .toList();
    }

    private long getTimestamp(LocalDateTime date) {
        return date.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    private String feedUserKey(long id) {
        return feedUserIdPrefix + id;
    }

    private String postIdKey(long id) {
        return postIdPrefix + id;
    }

    private String userIdKey(long id) {
        return userIdPrefix + id;
    }
}
