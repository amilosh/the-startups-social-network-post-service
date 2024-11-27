package faang.school.postservice.redis.service;

import faang.school.postservice.model.dto.PostDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PostCacheService {

    void savePostToCache(PostDto post);

    void addPostView(PostDto post);

    CompletableFuture<Void> saveAllPostsToCache(List<PostDto> posts);
}
