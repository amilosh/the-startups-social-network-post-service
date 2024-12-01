package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.FilterPostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.UpdatePostRequestDto;
import faang.school.postservice.dto.post.serializable.CommentCacheDto;
import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.album.Album;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PostMapper {
    @Value("${app.post.cache.news_feed.number_of_comments_limit}")
    private int numberOfCommentsInPostCacheDtoLimit;

    @Mapping(source = "resources", target = "resourceIds", qualifiedByName = "mapResourcesToResourceIds")
    @Mapping(target = "likes", expression = "java(post.getLikes() != null ? post.getLikes().size() : 0)")
    public abstract PostResponseDto toDto(Post post);

    public abstract List<PostResponseDto> toDtos(List<Post> post);

    public abstract Post toEntity(CreatePostRequestDto dto);

    public abstract Post toEntity(UpdatePostRequestDto dto);

    public abstract Post toEntity(FilterPostRequestDto dto);

    public abstract List<PostResponseDto> listEntitiesToListDto(List<Post> posts);

    public abstract PostResponseDto toDto(PostCacheDto postCacheDto);

    public  abstract List<PostResponseDto> postCacheDtoToPostResponseDto(List<PostCacheDto> postCacheDtos);

    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "mapLikes")
    @Mapping(source = "comments", target = "commentsCount", qualifiedByName = "mapComments")
    @Mapping(source = "albums", target = "albumIds", qualifiedByName = "mapAlbums")
    @Mapping(source = "resources", target = "resourceIds", qualifiedByName = "mapResources")
    @Mapping(source = "comments", target = "comments", qualifiedByName = "mapCommentsToDto")
    public abstract PostCacheDto toPostCacheDto(Post post);

    public abstract List<PostCacheDto> mapToPostCacheDtos(List<Post> posts);

    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "mapLikes")
    @Mapping(source = "post.id", target = "postId")
    public abstract CommentCacheDto toCommentCacheDto(Comment comment);

    @Named("mapLikes")
    protected Long mapLikes(List<Like> likes) {
        return (long) likes.size();
    }

    @Named("mapComments")
    protected Long mapComments(List<Comment> comments) {
        return (long) comments.size();
    }

    @Named("mapAlbums")
    protected Set<Long> mapAlbums(List<Album> albums) {
        return albums
                .stream()
                .map(Album::getId)
                .collect(Collectors.toSet());
    }

    @Named("mapResources")
    protected Set<Long> mapResources(List<Resource> resources) {
        return resources
                .stream()
                .map(Resource::getId)
                .collect(Collectors.toSet());
    }

    @Named("mapCommentsToDto")
    protected LinkedList<CommentCacheDto> mapCommentsToDto(List<Comment> comments) {
        return comments.stream()
                .filter(comment -> comment.getCreatedAt() != null)
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .limit(numberOfCommentsInPostCacheDtoLimit)
                .map(this::toCommentCacheDto)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Named("mapResourcesToResourceIds")
    protected List<Long> mapResourcesToResourceIds(List<Resource> resources) {
        if (resources == null) {
            return new ArrayList<>();
        }
        return resources.stream()
                .map(Resource::getId)
                .toList();
    }
}