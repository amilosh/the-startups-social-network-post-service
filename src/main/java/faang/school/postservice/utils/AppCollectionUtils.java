package faang.school.postservice.utils;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.IntStream;

@UtilityClass
public class AppCollectionUtils {
    public static <T> List<List<T>> getListOfBatches(List<T> list, Integer batchSize) {
        return IntStream.range(0, list.size())
                .filter(i -> i % batchSize == 0)
                .mapToObj(i -> list.subList(i, Math.min(i + batchSize, list.size())))
                .toList();
    }
}
