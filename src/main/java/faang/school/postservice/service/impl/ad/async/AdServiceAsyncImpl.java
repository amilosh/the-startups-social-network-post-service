package faang.school.postservice.service.impl.ad.async;

import faang.school.postservice.model.entity.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.service.AdServiceAsync;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdServiceAsyncImpl implements AdServiceAsync {
    private final AdRepository adRepository;

    @Override
    @Async("fixedThreadPool")
    public void deleteExpiredAdsByBatch(List<Ad> ads) {
        adRepository.deleteAllInBatch(ads);
    }
}