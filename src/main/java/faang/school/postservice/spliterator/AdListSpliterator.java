package faang.school.postservice.spliterator;

import faang.school.postservice.model.ad.Ad;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class AdListSpliterator implements Spliterator<Ad> {

    @Value("${ad.batch.size}")
    private int batchSize;

    @Override
    public List<List<Ad>> splitList(List<Ad> list) {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        if (list.size() <= batchSize) {
            return List.of(new ArrayList<>(list));
        }
        List<List<Ad>> subLists = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            subLists.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return subLists;
    }
}
