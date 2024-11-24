package faang.school.postservice.service;

import faang.school.postservice.dto.news.feed.NewsFeed;

public interface NewsFeedService {

    NewsFeed getNewsFeedBy(long userId);

    NewsFeed getNewsFeedBy(long userId, long firstPostId);
}
