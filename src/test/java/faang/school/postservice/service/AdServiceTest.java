package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.spliterator.Spliterator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdServiceTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private Spliterator<Ad> spliterator;

    @InjectMocks
    private AdService adService;

    @Test
    void shouldDeleteAllExpiredAdsInBatchesWhenThereAreExpiredAds() {
        when(adRepository.findAllExpiredAds(any(LocalDate.class))).thenReturn(Optional.of(setUpExpiredAds()));
        when(spliterator.splitList(setUpExpiredAds())).thenReturn(setUpBatches());

        adService.deleteAllExpiredAdsInBatches();

        verify(transactionService, times(1)).deleteExpiredAdsInBatch(setUpExpiredAds());
    }

    @Test
    void shouldDoNothingWhenThereAreNoExpiredAds() {
        when(adRepository.findAllExpiredAds(any(LocalDate.class))).thenReturn(Optional.of(Collections.emptyList()));

        adService.deleteAllExpiredAdsInBatches();

        verify(transactionService, times(0)).deleteExpiredAdsInBatch(any());
    }

    private List<Ad> setUpExpiredAds() {
        return List.of(new Ad(), new Ad());
    }

    private List<List<Ad>> setUpBatches() {
        return List.of(setUpExpiredAds());
    }
}