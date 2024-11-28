//package faang.school.postservice.repository;
//
//import faang.school.postservice.PostServiceApp;
//import faang.school.postservice.model.Like;
//import faang.school.postservice.model.Post;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest(classes = PostServiceApp.class)
//public class LikeRepositoryTest {
//
//    @Autowired
//    private LikeRepository likeRepository;
//
//    private Post post;
//
//    @BeforeEach
//    void setUp() {
//        post = new Post();
//        post.setId(101L);
//    }
//
//    @Test
//    public void testFindUserIdsByPostId() {
//        // Создаем лайки для поста
//        Like like1 = new Like(1L, 101L);
//        Like like2 = new Like(2L, 101L);
//        likeRepository.save(like1);
//        likeRepository.save(like2);
//
//        // Проверяем, что метод findUserIdsByPostId возвращает правильные userId
//        List<Long> userIds = likeRepository.findUserIdsByPostId(101L);
//
//        assertThat(userIds).containsExactlyInAnyOrder(1L, 2L);
//    }
//}
