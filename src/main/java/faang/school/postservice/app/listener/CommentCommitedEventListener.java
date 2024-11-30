package faang.school.postservice.app.listener;

import faang.school.postservice.kafka.producer.CommentKafkaProducer;
import faang.school.postservice.model.dto.CommentDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.event.CommentEvent;
import faang.school.postservice.model.event.application.CommentCommittedEvent;
import faang.school.postservice.model.event.kafka.CommentSentEvent;
import faang.school.postservice.redis.publisher.CommentEventPublisher;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentCommitedEventListener {
    private final CommentEventPublisher commentEventPublisher;
    private final PostRepository postRepository;
    private final CommentKafkaProducer commentKafkaProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentCommittedEvent(CommentCommittedEvent event) {
        CommentDto savedCommentDto = event.getCommentDto();
        commentEventPublisher.publish(createCommentEvent(savedCommentDto));
        CommentSentEvent commentSentEvent = new CommentSentEvent(
                savedCommentDto.getPostId(),
                savedCommentDto.getAuthorId(),
                savedCommentDto.getId(),
                savedCommentDto.getContent());
        commentKafkaProducer.sendEvent(commentSentEvent);
    }

    private CommentEvent createCommentEvent(CommentDto savedComment) {
        Long postId = savedComment.getPostId();
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new IllegalArgumentException("Post not found");
        }
        Post post = optionalPost.get();
        Long postAuthorId = post.getAuthorId();
        Long authorId = savedComment.getAuthorId();
        String postText = savedComment.getContent();
        Long commentId = savedComment.getId();
        return new CommentEvent(authorId, postAuthorId, postId, postText, commentId);
    }
}
