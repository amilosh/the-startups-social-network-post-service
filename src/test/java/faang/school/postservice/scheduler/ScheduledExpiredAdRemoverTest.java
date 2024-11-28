package faang.school.postservice.scheduler;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.ad.AdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduledExpiredAdRemoverTest {

    @Mock
    private AdService adService;

    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private ScheduledExpiredAdRemover scheduledExpiredAdRemover;

    @BeforeEach
    public void init() {
        int maxListSize = 1;
        ReflectionTestUtils.setField(scheduledExpiredAdRemover, "maxListSize", maxListSize);
    }

    @Test
    void deleteExpiredAdsScheduled_WithExpiredAds() {
        List<Ad> expiredAds = List.of(
            Ad.builder().id(1L).endDate(LocalDateTime.now().minusDays(1)).build(),
            Ad.builder().id(2L).endDate(LocalDateTime.now().minusDays(2)).build(),
            Ad.builder().id(3L).endDate(LocalDateTime.now().minusDays(3)).build()
        );

        when(adRepository.findAllExpiredAds()).thenReturn(expiredAds);

        scheduledExpiredAdRemover.deleteExpiredAdsScheduled();

        InOrder inOrder = inOrder(adRepository, adService);
        inOrder.verify(adRepository, times(1)).findAllExpiredAds();
        inOrder.verify(adService, times(3)).deleteAds(anyList()); // maxListSize=1, поэтому 3 вызова удаления
    }

    @Test
    void deleteExpiredAdsScheduled_NoExpiredAds() {
        when(adRepository.findAllExpiredAds()).thenReturn(Collections.emptyList());

        scheduledExpiredAdRemover.deleteExpiredAdsScheduled();

        verify(adRepository, times(1)).findAllExpiredAds();
        verify(adService, never()).deleteAds(anyList()); // Убедимся, что deleteAds не вызывается
    }
}
