package faang.school.postservice.service.feedheater;

import faang.school.postservice.model.event.HeatFeedCacheEvent;

public interface FeedHeater {
    void heat();

    void putAllFeeds(HeatFeedCacheEvent event);
}
