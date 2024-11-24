package faang.school.postservice.repository;

import faang.school.postservice.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Optional<Hashtag> findByTag(String tag);

    List<Hashtag> findAllByTagIn(List<String> hashtags);
}
