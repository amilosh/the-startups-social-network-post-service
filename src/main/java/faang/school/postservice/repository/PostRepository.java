package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerificationPostStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.id = :id AND p.deleted = false")
    Optional<Post> findByIdAndNotDeleted(@Param("id") Long id);

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    List<Post> findByVerificationStatus(VerificationPostStatus status);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND (p.scheduledAt IS NULL OR p.updatedAt > p.scheduledAt)")
    List<Post> findDraftsPaginate(Pageable pageable);

    @Query("SELECT p.authorId FROM Post p WHERE p.verificationStatus = :status GROUP BY p.authorId HAVING COUNT(*) > 5")
    List<Long> findAllUsersBorBan(VerificationPostStatus status);

    @Query(nativeQuery = true, value = """
            SELECT * FROM post
            WHERE hash_tags @> CAST(:hashTag AS jsonb)
            """)
    List<Post> findAllByHashTag(@Param("hashTag") String hashTag);

    @Query(nativeQuery = true, value = """
            SELECT * FROM post
            WHERE hash_tags @> CAST(:hashTag AS jsonb)
            AND published = true
            AND deleted = false
            ORDER BY published_at DESC LIMIT :number
            """)
    List<Post> findTopByHashTagByDate(@Param("hashTag") String hashTag, @Param("number") int number);

    @Query(nativeQuery = true, value = """
            SELECT * FROM post
            WHERE hash_tags @> CAST(:hashTag AS jsonb)
            AND published = true
            AND deleted = false
            ORDER BY published_at DESC
            LIMIT :limit
            OFFSET :offset
            """)
    List<Post> findInRangeByHashTagByDate(@Param("hashTag") String hashTag, @Param("offset") int offset, @Param("limit") int limit);

    @Query(
            value = """
                    select id
                    from post
                    where published is false
                    	and deleted is false
                    	and (scheduled_at is null or scheduled_at <= CURRENT_TIMESTAMP)
                    """,
            nativeQuery = true)
    List<Long> findReadyToPublishIds();

    @Query(value = "select * from post where id in (:ids)", nativeQuery = true)
    List<Post> findPostsByIds(@Param("ids") List<Long> ids);
}
