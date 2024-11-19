package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import jakarta.security.auth.message.MessagePolicy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "albums",ignore = true)
    Post toEntity(PostDto postDto);

    @Mapping(source = "likes", target = "likesIds", qualifiedByName = "likesToLikesIds")
    @Mapping(source = "comments", target = "commentsIds", qualifiedByName = "commentsToCommentsIds")
    @Mapping(source = "albums", target = "albumsIds", qualifiedByName = "albumsToAlbumsIds")
    PostDto toDto(Post post);

    @Mapping(target = "id" , ignore = true)
    @Mapping(target = "authorId" , ignore = true)
    @Mapping(target = "projectId" , ignore = true)
    void update(PostDto postDto, @MappingTarget Post post);

    @Named("likesToLikesIds")
    default List<Long> getLikesIds(List<Like> likes) {
        if (likes == null) {
            return new ArrayList<>();
        }

        return likes.stream()
                .map(Like::getId)
                .collect(Collectors.toList());
    }

    @Named("commentsToCommentsIds")
    default List<Long> getCommentsIds(List<Comment> comments) {
        if (comments == null) {
            return new ArrayList<>();
        }

        return comments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());
    }

    @Named("albumsToAlbumsIds")
    default List<Long> getAlbumsIds(List<Album> albums) {
        if (albums == null) {
            return new ArrayList<>();
        }

        return albums.stream()
                .map(Album::getId)
                .collect(Collectors.toList());
    }
}
