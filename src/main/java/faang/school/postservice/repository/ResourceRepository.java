package faang.school.postservice.repository;

import faang.school.postservice.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query("SELECT COUNT(*) FROM Resource r WHERE r.post.id = ?1")
    Integer countImagesForPostById(Long postId);

    @Query ("SELECT r FROM Resource r WHERE r.key = ?1")
    Resource findByKey(String key);

    @Modifying
    @Query ("DELETE FROM Resource r WHERE r.key = ?1")
    void deleteByKey(String key);

    @Query ("SELECT r.key FROM Resource r WHERE r.post.id = ?1")
    List<String> getAllKeysForPost(Long postId);

}
