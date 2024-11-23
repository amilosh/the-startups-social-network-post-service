package faang.school.postservice.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.event.LikeEventDto;
import faang.school.postservice.dto.kafka.event.PostEventDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.subscription.SubscriptionUserIdDto;
import faang.school.postservice.producer.KafkaPostProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostEventService {
    private final KafkaPostProducer producer;
    private final KafkaSerializer kafkaSerializer;
    private final UserServiceClient userServiceClient;

    @Value("${spring.data.kafka.producer.producers.post.limit-amount-user-ids}")
    private final int limitAmountUserIds;

    /* TODO:
    Во время ручного теста тут вылезло в Postmane: "No bean named 'kafkaProducerExecutor' available: No matching
    Executor bean found for qualifier 'kafkaProducerExecutor' - neither qualifier match nor bean name match!"
    подумать как такое не кидать пользователю
     */
    //@Async("kafkaProducerConsumerExecutor") почему-то если сделать, то будет ошибка
    // TODO: спросить вопрос что делать, если в процессе публикации в кафка возникла какая-то ошибка
    // как потом искать эти фиды
    public void produce(PostDto postDto) {
        Long followeeId = postDto.authorId();
        if (followeeId == null) {
            return;
        }

        List<SubscriptionUserIdDto> followerDtos;
        long lastId = 0L;
        do {
            //followerDtos = userServiceClient.getFollowerIds(followeeId, lastId, limitAmountUserIds);
            followerDtos = new ArrayList<>(Arrays.asList(
                new SubscriptionUserIdDto(2L),
                new SubscriptionUserIdDto(3L),
                new SubscriptionUserIdDto(4L)
            ));

            if (followerDtos.isEmpty()) {
                break;
            }
            List<Long> followerIds = followerDtos.stream()
                .map(SubscriptionUserIdDto::followerId)
                .toList();
            lastId = followerIds.get(followerIds.size() - 1);

            PostEventDto eventDto = createPostEventDto(followeeId, postDto, followerIds);
            String event = kafkaSerializer.serialize(eventDto);
            producer.send(event);
            followerDtos.clear();
        } while(!followerDtos.isEmpty());
    }

    private PostEventDto createPostEventDto(Long followeeId, PostDto postDto, List<Long> followerIds) {
        return PostEventDto.builder()
            .authorId(followeeId)
            .postId(postDto.id())
            .followerIds(followerIds)
            .build();
    }
}
