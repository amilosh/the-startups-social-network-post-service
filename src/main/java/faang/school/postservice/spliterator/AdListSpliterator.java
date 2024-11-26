package faang.school.postservice.spliterator;

import faang.school.postservice.model.ad.Ad;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class AdListSpliterator implements Spliterator<Ad> {

    @Value("${ad.batch.size}")
    private int batchSize;

    @Override
    public List<List<Ad>> splitList(List<Ad> list) {
        if (list.size() <= batchSize) {
            return List.of(list);
        }
        List<List<Ad>> subLists = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            subLists.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return subLists;
    }
}
