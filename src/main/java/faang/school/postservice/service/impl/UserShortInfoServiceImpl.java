package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.dto.UserWithoutFollowersDto;
import faang.school.postservice.model.entity.UserShortInfo;
import faang.school.postservice.repository.UserShortInfoRepository;
import faang.school.postservice.service.UserShortInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserShortInfoServiceImpl implements UserShortInfoService {
    private static final int REFRESH_TIME_IN_HOURS = 3;
    @Value("${system-user-id}")
    private int systemUserId;
    private final UserShortInfoRepository userShortInfoRepository;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;

    @Transactional
    @Override
    public UserShortInfo updateUserShortInfoIfStale(Long userId) {
        userContext.setUserId(systemUserId);

        return userShortInfoRepository.findById(userId)
                .filter(info -> info.getLastSavedAt().isAfter(LocalDateTime.now().minusHours(REFRESH_TIME_IN_HOURS)))
                .orElseGet(() -> {
                    UserWithoutFollowersDto userWithoutFollowers = userServiceClient.getUserWithoutFollowers(userId);
                    UserShortInfo newUserShortInfo = new UserShortInfo(
                            userId,
                            userWithoutFollowers.getUsername(),
                            userWithoutFollowers.getFileId(),
                            userWithoutFollowers.getSmallFileId(),
                            null
                    );
                    return userShortInfoRepository.save(newUserShortInfo);
                });
    }
}
