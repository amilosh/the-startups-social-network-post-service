package faang.school.postservice.service;

import faang.school.postservice.model.dto.FeedDto;
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
        System.out.println("------------");
        feedPosts.forEach(System.out::println);
        Map<Long, AuthorCache> authorCachesMap = getAuthors(authorIds);
        System.out.println("+++++++++++++++++++");
        authorCachesMap.forEach((key, value) -> System.out.println(key + " : " + value));
        return null;
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
