package faang.school.postservice.mapper;

import faang.school.postservice.cache.PostCache;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostCacheMapper {

    PostCache toPostCache(Post post);

    @Named("mapLikeToIds")
    static List<Long> mapLikeToIds(List<Like> likes) {
        if (likes == null) {
            return null;
        } else {
            return likes.stream()
                    .map(Like::getId)
                    .toList();
        }
    }

    @Named("mapCommentToIds")
    static List<Long> mapCommentToIds(List<Comment> comments) {
        if (comments == null) {
            return null;
        } else {
            return comments.stream()
                    .map(Comment::getId)
                    .toList();
        }
    }
}
