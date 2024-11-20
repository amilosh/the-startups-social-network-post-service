package faang.school.postservice.repository;

import faang.school.postservice.model.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeedCacheRepository extends JpaRepository<Feed, Long> {
    Optional<Feed> findByUserId(long userId);
}
