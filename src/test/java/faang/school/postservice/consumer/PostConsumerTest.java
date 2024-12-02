package faang.school.postservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.message.PostEvent;
import faang.school.postservice.service.newsFeed.FeedService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PostConsumerTest {

    @InjectMocks
    private PostConsumer postConsumer;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FeedService feedService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void listenEventPost_WhenArgsValid_successfulCompletion() throws Exception {
        String message = "{"
                + "\"postId\": 12345,"
                + "\"authorId\": 67890,"
                + "\"publishedAt\": \"2023-10-25T14:30:00\","
                + "\"subscribers\": [1001, 1002, 1003, 1004]"
                + "}";

        PostEvent postEvent = PostEvent.builder()
                .postId(12345)
                .authorId(67890)
                .publishedAt(LocalDateTime.of(2023, 10, 25, 14, 30))
                .subscribers(List.of(1001L, 1002L, 1003L, 1004L))
                .build();

        when(objectMapper.readValue(message, PostEvent.class)).thenReturn(postEvent);

        postConsumer.listenEventPost(message, acknowledgment);

        verify(objectMapper, times(1)).readValue(message, PostEvent.class);
        verify(feedService, times(1)).addPostToFeed(postEvent);
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void listenEventPost_WhenInvalidMessage_ShouldLogError() throws Exception {
        String message = "invalid_json";
        when(objectMapper.readValue(message, PostEvent.class)).thenThrow(new RuntimeException("Parsing error"));

        postConsumer.listenEventPost(message, acknowledgment);

        verify(objectMapper, times(1)).readValue(message, PostEvent.class);
        verify(feedService, never()).addPostToFeed(any());
        verify(acknowledgment, never()).acknowledge();
    }
}
