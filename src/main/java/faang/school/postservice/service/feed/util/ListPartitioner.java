package faang.school.postservice.service.feed.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class ListPartitioner {
    public <T> List<List<T>> exec(List<T> list, int maxInPartition) {
        List<List<T>> groups = new ArrayList<>();
        int numberOfGroups = (int) Math.ceil(((double) list.size() / maxInPartition));

        IntStream.range(0, numberOfGroups).forEach(i -> {
            int offset = maxInPartition * i;
            int limit = Math.min((offset + maxInPartition), list.size());

            List<T> group = list.subList(offset, limit);
            groups.add(group);
        });

        return groups;
    }
}
