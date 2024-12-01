package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.request.PostCreationRequest;
import faang.school.postservice.dto.redis.cache.PostCacheDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toPostFromCreationRequest(PostCreationRequest postCreationRequest);

    @Mapping(source = "likes", target = "likes", qualifiedByName = "getLikesCount")
    PostDto toPostDto(Post post);

    List<PostDto> toPostDtoList(List<Post> posts);

    @Mapping(source = "id", target = "postId")
    PostViewEvent toPostViewEvent(Post post);

    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "getLikesCount")
    @Mapping(source = "likes", target = "likes", qualifiedByName = "getLikeIds")
    @Mapping(source = "comments", target = "commentsCount", qualifiedByName = "getCommentsCount")
    @Mapping(source = "comments", target = "comments", qualifiedByName = "getCommentIds")
    PostCacheDto toPostCacheDto(Post post);

    @Named("getLikesCount")
    default int getLikesCount(List<Like> likes) {
        return likes.size();
    }

    @Named("getCommentsCount")
    default int getCommentsCount(List<Comment> comments) {
        return comments.size();
    }

    @Named("getLikeIds")
    default Set<Long> getLikeIds(List<Like> likes) {
        if (likes == null) {
            return new HashSet<>();
        }
        return likes.stream()
                .map(Like::getId)
                .collect(Collectors.toSet());
    }

    @Named("getCommentIds")
    default Set<Long> getCommentIds(List<Comment> comments) {
        if (comments == null) {
            return new HashSet<>();
        }
        return comments.stream()
                .map(Comment::getId)
                .collect(Collectors.toSet());
    }
}