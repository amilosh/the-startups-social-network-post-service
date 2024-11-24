package faang.school.postservice.mapper;

import faang.school.postservice.model.dto.CommentDto;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.LikeService;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class RedisPostDtoMapper {

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    private static final int RECENT_COMMENTS_LIMIT = 3;

    @Mapping(source = "id", target = "postId")
    @Mapping(source = "createdAt", target = "createdAt")
    public abstract RedisPostDto mapToRedisPostDto(PostDto postDto);

    @AfterMapping
    protected void fillAdditionalFields(@MappingTarget RedisPostDto redisPostDto, PostDto postDto) {
        redisPostDto.setCommentCount((int) commentService.getCommentCount(postDto.getId()));

        redisPostDto.setRecentComments(
                commentService.getRecentComments(postDto.getId(), RECENT_COMMENTS_LIMIT).stream()
                        .map(CommentDto::getContent)
                        .toList()
        );

        redisPostDto.setLikeCount(likeService.getLikeCount(postDto.getId()));
    }
}

