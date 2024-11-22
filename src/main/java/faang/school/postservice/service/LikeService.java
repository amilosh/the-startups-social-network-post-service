package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.NetworkException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {
    private static final int BATCH_SIZE = 100;
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    public List<UserDto> getUsersWhoLikePostByPostId(long Id) {
        List<Like> usersWhoLikedPost = likeRepository.findByPostId(Id);
        return mapLikesToUserDtos(usersWhoLikedPost);
    }


    public List<UserDto> getUsersWhoLikeComments(long id) {
        List<Like> usersWhoLikedComment = likeRepository.findByCommentId(id);
        return mapLikesToUserDtos(usersWhoLikedComment);
    }

    private List<UserDto> mapLikesToUserDtos(List<Like> usersWhoLiked) {
        List<Long> userIds = usersWhoLiked.stream()
                .map(Like::getUserId)
                .toList();

        List<List<Long>> userIdBatches = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, userIds.size());
            userIdBatches.add(userIds.subList(i, endIndex));
        }

        List<CompletableFuture<List<UserDto>>> futures = userIdBatches.stream()
                .map(batch -> CompletableFuture.supplyAsync(() -> fetchUserDtosSafely(batch))
                        .exceptionally(ex -> {
                            log.error("Error fetching users for batch {}: {}", batch, ex.getMessage(), ex);
                            return new ArrayList<>();
                        }))
                .collect(Collectors.toList());

        return collectResultsFromFutures(futures);
    }

    private List<UserDto> fetchUserDtosSafely(List<Long> batch) {
        List<UserDto> userDtos = new ArrayList<>();
        batch.forEach(userId -> userDtos.add(userServiceClient.getUser(userId)));

        return userDtos;
    }

    private List<UserDto> collectResultsFromFutures(List<CompletableFuture<List<UserDto>>> futures) {
        List<UserDto> collectedResults = new ArrayList<>();

        for (CompletableFuture<List<UserDto>> future : futures) {
            try {
                collectedResults.addAll(future.join());
            } catch (CompletionException e) {
                log.error("Error while joining CompletableFuture: {}", e.getMessage(), e);
                throw new RuntimeException("Error retrieving user DTOs", e.getCause());
            } catch (Exception e) {
                log.error("Unexpected error while collecting results: {}", e.getMessage(), e);
                throw new RuntimeException("Unexpected error occurred during the process", e);
            }
        }

        return collectedResults;
    }
}
