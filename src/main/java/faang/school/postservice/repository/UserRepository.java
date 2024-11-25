package faang.school.postservice.repository;

import faang.school.postservice.model.User;
import faang.school.postservice.model.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(nativeQuery = true,
            value = """
                     select p.* from subscription s
                     join post p on s.followee_id = p.author_id where
                     s.follower_id = :userId order by p.published_at;
                     """
    )
    List<Post> getPostsForFeedById(@Param("userId") long userId);

    @Query(nativeQuery = true,
            value = """
                     select id from users
                     where active = true;
                     """
    )
    List<Long> getActiveUsersId();
}
