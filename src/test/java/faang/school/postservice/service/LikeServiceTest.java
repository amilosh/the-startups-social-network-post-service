package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeValidator likeValidator;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @Spy
    private LikeMapperImpl likeMapper;

    @Test
    public void testGetLikeByIdUserNotExists() {
        long userId = 1L;
        when(likeRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> likeService.getLikeById(userId));
    }

    @Test
    public void testGetLikeById() {
        long userId = 1L;
        Like like = new Like();
        when(likeRepository.findById(userId)).thenReturn(Optional.of(like));

        likeService.getLikeById(userId);
    }

    @Test
    public void testGetAllLikes() {
        List<Like> likes = List.of(new Like(), new Like());
        when(likeRepository.findAll()).thenReturn(likes);
        likeService.getAllLikes();
    }

    @Test
    public void testAddLike() {
        long userId = 1L;
        long postId = 2L;
        LikeDto likeDto = new LikeDto();
        likeDto.setPostId(postId);

        when(userContext.getUserId()).thenReturn(userId);
        when(postService.getPost(postId)).thenReturn(new Post());

        likeService.addLike(likeDto);

        verify(likeRepository).save(any(Like.class));
    }

    @Test
    public void testDeleteLike() {
        LikeDto likeDto = new LikeDto();
        likeDto.setId(1L);

        when(likeRepository.existsById(likeDto.getId())).thenReturn(true);

        likeService.deleteLike(likeDto);
        verify(likeRepository).deleteById(likeDto.getId());
    }
}
