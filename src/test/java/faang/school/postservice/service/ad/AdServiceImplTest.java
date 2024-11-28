package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class AdServiceImplTest {
    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AdServiceImpl adService;

    @Test
    void deleteAds() {
        List<Ad> ids = List.of();

        adService.deleteAds(ids);

        InOrder inOrder = inOrder(adRepository);
        inOrder.verify(adRepository, times(1)).deleteAll(ids);
    }
}
