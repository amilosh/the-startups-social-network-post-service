package faang.school.postservice.repository;

import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Test
    @Transactional
    public void testFindUserIdsByPostId() {
        // Создаем пост
        Post post = new Post();
        post.setId(101L);  // Предположим, что это ID поста

        
        // В вашем коде это возможно будет выглядеть как `postRepository.save(post);`

        // Создаем лайки для поста
        Like like1 = new Like();
        like1.setUserId(1L);
        like1.setPost(post); // Привязываем лайк к посту
        likeRepository.save(like1);

        Like like2 = new Like();
        like2.setUserId(2L);
        like2.setPost(post); // Привязываем лайк к посту
        likeRepository.save(like2);

        // Проверяем, что метод findUserIdsByPostId возвращает правильные userId
        List<Long> userIds = likeRepository.findUserIdsByPostId(101L);

        assertThat(userIds).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @Transactional
    public void testFindUserIdsByCommentId() {
        // Создаем комментарий для теста
        Comment comment = new Comment();
        comment.setId(1L);  // Предположим, что это ID комментария

        // Сохраняем комментарий в базе данных
        // В вашем коде это возможно будет выглядеть как `commentRepository.save(comment);`

        // Создаем лайки для комментария
        Like like1 = new Like();
        like1.setUserId(1L);
        like1.setComment(comment); // Привязываем лайк к комментарию
        likeRepository.save(like1);

        Like like2 = new Like();
        like2.setUserId(2L);
        like2.setComment(comment); // Привязываем лайк к комментарию
        likeRepository.save(like2);

        // Проверяем, что метод findUserIdsByCommentId возвращает правильные userId
        List<Long> userIds = likeRepository.findUserIdsByCommentId(1L);

        assertThat(userIds).containsExactlyInAnyOrder(1L, 2L);
    }
}
