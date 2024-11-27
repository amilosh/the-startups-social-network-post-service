package faang.school.postservice.mapper.comment;

import faang.school.postservice.cache.CommentCache;
import faang.school.postservice.message.CommentPublishMessage;
import faang.school.postservice.model.Comment;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Comparator.comparing;

@Component
public class CommentCacheMapper {

    public List<CommentCache> toLimitedCommentCache(List<Comment> comments, int limit) {
        return comments.stream()
                .sorted(comparing(Comment::getCreatedAt).reversed())
                .limit(limit)
                .map(this::toCommentCache)
                .toList();
    }

    public CommentCache toCommentCache(Comment comment) {
        return CommentCache.builder()
                .id(comment.getId())
                .authorId(comment.getAuthorId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public CommentCache toCommentCache(CommentPublishMessage commentPublishMessage) {
        return CommentCache.builder()
                .id(commentPublishMessage.getCommentId())
                .content(commentPublishMessage.getContent())
                .createdAt(commentPublishMessage.getCreatedAt())
                .authorId(commentPublishMessage.getCommentAuthorId())
                .build();
    }

    public CommentPublishMessage toCommentPublishMessage(Comment comment) {
        return CommentPublishMessage.builder()
                .commentId(comment.getId())
                .postId(comment.getPost().getId())
                .commentAuthorId(comment.getAuthorId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
