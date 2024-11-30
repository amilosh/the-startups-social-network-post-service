package faang.school.postservice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class ViewBuffer {

    private final ConcurrentHashMap<Long, Long> views = new ConcurrentHashMap<>();

    public void addView(Long postId) {
        views.compute(postId, (key, value) -> isNull(value) ? 1L : value + 1);
    }

    public synchronized Map<Long, Long> getViewsAndClear() {
        Map<Long, Long> updatedViews = new ConcurrentHashMap<>(views);
        views.clear();

        return updatedViews;
    }

}
