package faang.school.postservice.mapper;

import faang.school.postservice.cache.PostCache;
import faang.school.postservice.mapper.comment.CommentCacheMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ResourceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostCacheMapper {
    private final CommentCacheMapper commentCacheMapper;

    @Value("${spring.data.redis.cache.comment.limit}")
    private int commentLimit;

    public PostCache toPostCache(Post post) {
        return PostCache.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .content(post.getContent())
                .resourceKeys(toResourceKeys(post.getResourceEntities()))
                .comments(commentCacheMapper.toLimitedCommentCache(post.getComments(), commentLimit))
                .commentsCount((long) post.getComments().size())
                .likeCount((long) post.getLikes().size())
                .viewCount(post.getView().getViewCount())
                .publishedAt(post.getPublishedAt())
                .build();
    }

    private List<String> toResourceKeys(List<ResourceEntity> resources) {
        return resources.stream()
                .map(ResourceEntity::getKey)
                .toList();
    }
}