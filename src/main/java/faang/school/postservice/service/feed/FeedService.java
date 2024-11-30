package faang.school.postservice.service.feed;

import faang.school.postservice.annotations.publisher.PublishEvent;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.serializable.CommentCacheDto;
import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.cache.CommentCacheRepository;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.repository.cache.UserCacheRepository;
import faang.school.postservice.repository.cache.util.key.PostKey;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import static faang.school.postservice.enums.publisher.PublisherType.POST_VIEW;

@RequiredArgsConstructor
@Service
public class FeedService {
    private final CommentCacheRepository commentCacheRepository;
    private final PostCacheRepository postCacheRepository;
    private final UserCacheRepository userCacheRepository;
    private final UserServiceClient userServiceClient;
    private final FeedHeaterService feedHeaterService;
    private final Executor usersFeedsUpdatePool;
    private final PostService postService;
    private final PostKey postKey;

    @PublishEvent(type = POST_VIEW)
    public List<PostCacheDto> getSetOfPosts(long userId, long offset, long limit) {
        Set<String> postIds = userCacheRepository.findPostIdsInUserFeed(userId, offset, limit);

        if (!postIds.isEmpty()) {
            return findPostsInCache(postIds);
        }

        List<PostCacheDto> postDtoList = postService.getSetOfPosts(userId, offset, limit);
        setAuthors(postDtoList);

        usersFeedsUpdatePool.execute(() -> feedHeaterService.updateUserFeed(userId));

        return postDtoList;
    }

    private List<PostCacheDto> findPostsInCache(Set<String> postIds) {
        List<PostCacheDto> postDtoList = postIds.stream()
                .map(key -> postCacheRepository.findByKey(key).orElseGet(() ->
                        findSaveToCacheAndGetPostCacheDto(key)))
                .toList();

        setAuthors(postDtoList);

        return postDtoList;
    }

    private void setAuthors(List<PostCacheDto> postDtoList) {
        postDtoList.forEach(postDto -> {
            postDto.setAuthorDto(userCacheRepository.findById(postDto.getAuthorId()).orElseGet(() ->
                    findSaveToCacheAndGetUserDto(postDto.getAuthorId())));

            List<CommentCacheDto> commentDtoList = commentCacheRepository.findAllByPostId(postDto.getId());

            if (commentDtoList.isEmpty()) {
                commentDtoList = postDto.getComments();
            }

            commentDtoList.forEach(comment ->
                    comment.setAuthorDto(userCacheRepository.findById(comment.getAuthorId()).orElseGet(() ->
                            findSaveToCacheAndGetUserDto(comment.getAuthorId()))));

            postDto.setComments(commentDtoList);
        });
    }

    private UserDto findSaveToCacheAndGetUserDto(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        userCacheRepository.save(userDto);
        return userDto;
    }

    private PostCacheDto findSaveToCacheAndGetPostCacheDto(String key) {
        long id = postKey.getPostIdFrom(key);
        PostCacheDto postDto = postService.findPostDtoById(id);

        postCacheRepository.save(postDto);
        commentCacheRepository.saveAll(postDto.getComments());

        return postDto;
    }
}
