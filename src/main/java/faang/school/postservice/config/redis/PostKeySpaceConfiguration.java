package faang.school.postservice.config.redis;

import faang.school.postservice.model.post.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PostKeySpaceConfiguration extends KeyspaceConfiguration {
    @Value("${feed.cache.post.time-to-live-in-seconds}")
    private long timeToLive;

    @Override
    protected Iterable<KeyspaceSettings> initialConfiguration() {
        KeyspaceSettings keyspaceSettings = new KeyspaceSettings(Post.class, "post");
        keyspaceSettings.setTimeToLive(timeToLive);
        return Collections.singleton(keyspaceSettings);
    }
}
