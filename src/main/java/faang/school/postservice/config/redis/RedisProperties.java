package faang.school.postservice.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {

    private String host;
    private int port;
    private Channels channels;
    private Ttl ttl;


    @Getter
    @Setter
    protected static class Channels {
        private Channel calculationsChannel;
        private Channel likePostChannel;
        private Channel newCommentChannel;
        private Channel commentChannel;
        private Channel postViewChannel;
        private Channel userBanChannel;

        @Getter
        @Setter
        protected static class Channel {
            private String name;
        }
    }

    @Getter
    @Setter
    protected static class Ttl {
        private int basicTtl;
        private int postTtl;
        private int commentTtl;
        private int likePostTtl;
    }
}