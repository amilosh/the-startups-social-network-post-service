package faang.school.postservice.repository;

import faang.school.postservice.model.entity.UserShortInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserShortInfoRepository extends JpaRepository<UserShortInfo, Long> {

    List<UserShortInfo> findAllBySavedDateTimeAfter(LocalDateTime dateTime);
}
