package faang.school.postservice.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubscriptionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Long> findFollowersIdByFolloweeId(Long followeeId) {
        String sql = "SELECT follower_id FROM subscription WHERE followee_id = :followeeId";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("followeeId", followeeId);
        return query.getResultList();
    }
}

