package faang.school.postservice.config.redis;

import faang.school.postservice.model.comment.CommentRedis;
import faang.school.postservice.model.post.PostRedis;
import faang.school.postservice.model.user.UserRedis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;
import org.springframework.data.redis.core.convert.MappingConfiguration;
import org.springframework.data.redis.core.index.IndexConfiguration;
import org.springframework.data.redis.core.mapping.RedisMappingContext;

import java.util.List;

@Configuration
public class RedisKeyspaceConfig {
    @Value("${redis.ttl.user}")
    private Long userTtl;

    @Value("${redis.ttl.post}")
    private Long postTtl;

    @Value("${redis.ttl.comment}")
    private Long commentTtl;

    @Bean
    public RedisMappingContext keyValueMappingContext() {
        return new RedisMappingContext(new MappingConfiguration(new IndexConfiguration(), keyspaceConfiguration()));
    }

    @Bean
    public KeyspaceConfiguration keyspaceConfiguration() {
        return new KeyspaceConfiguration() {
            @Override
            protected Iterable<KeyspaceSettings> initialConfiguration() {
                KeyspaceSettings userKeyspace = new KeyspaceSettings(UserRedis.class, "user");
                userKeyspace.setTimeToLive(userTtl);

                KeyspaceSettings postKeyspace = new KeyspaceSettings(PostRedis.class, "post");
                postKeyspace.setTimeToLive(postTtl);

                KeyspaceSettings commentKeyspace = new KeyspaceSettings(CommentRedis.class, "comment");
                commentKeyspace.setTimeToLive(commentTtl);

                return List.of(userKeyspace, postKeyspace, commentKeyspace);
            }
        };
    }
}
