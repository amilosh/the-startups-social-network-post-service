package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.AcceptanceLikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.like_validator.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Spy
    private LikeMapperImpl mapper;
    @Mock
    private LikeValidator validator;

    private AcceptanceLikeDto acceptanceLikeDto;
    private Post post;
    private Like like;

    @BeforeEach
    public void setUp() {
        acceptanceLikeDto = AcceptanceLikeDto.builder()
                .userId(1L)
                .build();
        post = new Post();
        post.setId(1L);
        post.setLikes(new ArrayList<>());
        like.setId(1L);
    }


    @Test
    public void testPostLikeSuccess() {
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(post));
        when(validator.validatePostHatLike(1L, 1L)).thenReturn(true);

        likeService.postLike(acceptanceLikeDto, 1L);

        verify(likeRepository).save(like);
        verify(postRepository).save(post);

        assertEquals(like.getPost(), post);

    }




}
