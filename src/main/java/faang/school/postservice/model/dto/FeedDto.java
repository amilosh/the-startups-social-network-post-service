package faang.school.postservice.model.dto;

import faang.school.postservice.redis.model.entity.PostCache;

import java.util.LinkedList;

public class FeedDto {
    private Long id;
    private Long ownerId;
    private LinkedList<PostCache> postCaches;
}
