package faang.school.postservice.repository;

import faang.school.postservice.model.post.PostViews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostViewsRepository extends JpaRepository<PostViews, Long> {
    Optional<PostViews> findByPostId(Long postId);
}
