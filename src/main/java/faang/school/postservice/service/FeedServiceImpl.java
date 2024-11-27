package faang.school.postservice.service;

import faang.school.postservice.redis.model.dto.FeedDto;
import faang.school.postservice.redis.mapper.AuthorCacheMapper;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.model.dto.AuthorRedisDto;
import faang.school.postservice.redis.model.dto.PostRedisDto;
import faang.school.postservice.redis.model.entity.AuthorCache;
import faang.school.postservice.redis.model.entity.FeedCache;
import faang.school.postservice.redis.model.entity.PostCache;
import faang.school.postservice.redis.repository.AuthorCacheRedisRepository;
import faang.school.postservice.redis.repository.FeedsCacheRepository;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    // TODO set POST_NUMBER to 20
    private final int POSTS_NUMBER = 2;
    private final FeedsCacheRepository feedsCacheRepository;
    private final PostCacheRedisRepository postCacheRedisRepository;
    private final AuthorCacheRedisRepository authorCacheRedisRepository;
    private final PostCacheMapper postCacheMapper;
    private final AuthorCacheMapper authorCacheMapper;

    @Override
    public FeedDto getFeed(Long feedId, Long userId, Integer startPostId) {
        if (startPostId == null) {
            startPostId = 0;
        }
        FeedCache requestedFeed = null;
        try {
            requestedFeed = feedsCacheRepository.findById(feedId).orElseThrow(() -> {
                log.info("Feed with id " + feedId + " not found in Redis");
                return new NoSuchElementException("Feed with id " + feedId + " not found");
            });
        } catch (NoSuchElementException e) {
            log.info("Try to make feed from Postgres");
            // TODO asdf
        }
        List<Long> sublist = getSubList(requestedFeed.getPostIds(), startPostId, POSTS_NUMBER);
        List<PostCache> feedPosts = getPosts(sublist);
        Set<Long> authorIds = feedPosts.stream().map(PostCache::getAuthorId).collect(Collectors.toSet());

        Map<Long, AuthorCache> authorCaches = getAuthors(authorIds);
        Map<Long, AuthorRedisDto> authorRedisDtos = new HashMap<>();
        for (var author : authorCaches.entrySet()) {
            authorRedisDtos.put(author.getKey(), authorCacheMapper.toAuthorRedisDto(author.getValue()));
        }

        List<PostRedisDto> postRedisDtos = feedPosts.stream().map(post -> {
            PostRedisDto postRedisDto = postCacheMapper.toPostRedisDto(post);
            postRedisDto.setAuthor(authorRedisDtos.get(post.getAuthorId()));
            return postRedisDto;
        }).toList();
        return FeedDto.builder()
                .postRedisDtos(postRedisDtos)
                .id(requestedFeed.getId())
                .build();
    }

    private List<PostCache> getPosts(List<Long> postIds) {
        return StreamSupport
                .stream(postCacheRedisRepository.findAllById(postIds).spliterator(), false)
                .toList();
    }

    private List<Long> getSubList(List<Long> originalList, int startIndex, int count) {
        if (originalList == null || originalList.size() < startIndex || count < 0) {
            throw new IllegalArgumentException("Invalid start index or count");
        }
        int endIndex = Math.min(startIndex + count, originalList.size());

        return originalList.subList(startIndex, endIndex);
    }

    private Map<Long, AuthorCache> getAuthors(Set<Long> authorIds) {
        Map<Long, AuthorCache> authorCachesMap = new HashMap<>();
        List<AuthorCache> authorCaches = StreamSupport
                .stream(authorCacheRedisRepository.findAllById(authorIds).spliterator(), false)
                .toList();
        authorCaches.forEach(author -> authorCachesMap.put(author.getId(), author));
        return authorCachesMap;
    }
}
