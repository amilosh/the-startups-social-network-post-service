package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.spliterator.Spliterator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {
    private static final String TASK_EXECUTOR = "taskExecutor";

    private final AdRepository adRepository;
    private final TransactionService transactionService;
    private final Spliterator<Ad> spliterator;

    @Async(TASK_EXECUTOR)
    public void deleteAllExpiredAdsInBatches() {
        getExpiredAdsInBatches().forEach(this::processBatch);
    }

    private List<Ad> getExpiredAds() {
        return adRepository.findAllExpiredAds(LocalDate.now()).orElse(Collections.emptyList());
    }

    private List<List<Ad>> getExpiredAdsInBatches() {
        return spliterator.splitList(getExpiredAds());
    }

    private void processBatch(List<Ad> batch) {
        transactionService.deleteExpiredAdsInBatch(batch);
    }
}
