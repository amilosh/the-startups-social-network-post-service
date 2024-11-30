package faang.school.postservice.repository;

import faang.school.postservice.model.ViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ViewRepository extends JpaRepository<ViewEntity, Long> {

    Optional<ViewEntity> findByPostId(Long postId);
}
