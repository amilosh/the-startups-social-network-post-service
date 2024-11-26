package faang.school.postservice.repository;

import faang.school.postservice.model.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(long postId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    int countByPostId(long postId);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    List<Comment> findRecentByPostId(long postId, Pageable pageable);
}
