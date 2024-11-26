package faang.school.postservice.service.post.redis;

import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.model.post.PostRedis;
import faang.school.postservice.repository.redis.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostRedisService {
    private final PostMapper postMapper;
    private final PostRedisRepository postRedisRepository;

    public void savePostsToRedis(List<Post> posts) {
        List<PostRedis> postDtos = postMapper.mapToPostRedisList(posts);
        postRedisRepository.saveAll(postDtos);
    }
}
