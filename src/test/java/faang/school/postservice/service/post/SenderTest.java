package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.properties.BatchProperties;
import faang.school.postservice.dto.post.message.PostEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.kafkaProducer.PostEventProducer;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SenderTest {

    private Post post;
    private List<Long> subscribers;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostEventProducer postEventProducer;

    @Mock
    private BatchProperties batchProperties;

    @InjectMocks
    private Sender sender;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .content("content")
                .authorId(2L)
                .projectId(3L)
                .scheduledAt(LocalDateTime.of(2021, 7, 15, 10, 30))
                .build();

        subscribers = List.of(1L, 2L, 3L);
    }

    @Test
    void testBatchSending_WhenArgsValid_SuccessfulCompletion() {
        when(userServiceClient.getUserSubscribers(post.getAuthorId())).thenReturn(subscribers);
        when(batchProperties.getBatchSizeSubscribers()).thenReturn(10);

        sender.batchSending(post);

        verify(userServiceClient).getUserSubscribers(post.getAuthorId());

        ArgumentCaptor<PostEvent> captor = ArgumentCaptor.forClass(PostEvent.class);
        verify(postEventProducer).sendEvent(captor.capture());

        PostEvent postEventCaptor = captor.getValue();

        assertEquals(postEventCaptor.getPostId(), post.getId());
        assertEquals(postEventCaptor.getAuthorId(), post.getAuthorId());
        assertEquals(postEventCaptor.getSubscribers(), subscribers);
    }

    @Test
    void testCreatePost_WhenUserSubscribersIsEmpty_ReturnIllegalArgumentException() {
        when(userServiceClient.getUserSubscribers(post.getAuthorId())).thenReturn(Collections.emptyList());

        LogCaptor logCaptor = LogCaptor.forClass(Sender.class);

        sender.batchSending(post);

        assertTrue(logCaptor.getWarnLogs().contains("No subscribers found for post's author: 2"),
                "It was expected that logging at the WARN level would contain a message about the absence of subscribers");

        verify(postEventProducer, never()).sendEvent(any());
    }
}
