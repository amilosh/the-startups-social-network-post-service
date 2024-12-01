package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.spliterator.Partitioner;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdService {
    private static final String ASYNC_CFG_EXECUTOR_BEAN_NAME = "taskExecutor";

    private final AdRepository adRepository;
    private final TransactionService transactionService;
    private final Partitioner<Ad> partitioner;

    @Async(ASYNC_CFG_EXECUTOR_BEAN_NAME)
    public void deleteAllExpiredAdsInBatches() {
        getExpiredAdsInBatches().forEach(this::processBatch);
    }

    private List<Ad> getExpiredAds() {
        return adRepository.findAllExpiredAds(LocalDate.now());
    }

    private List<List<Ad>> getExpiredAdsInBatches() {
        return partitioner.splitList(getExpiredAds());
    }

    private void processBatch(List<Ad> batch) {
        transactionService.deleteExpiredAdsInBatch(batch);
    }
}
