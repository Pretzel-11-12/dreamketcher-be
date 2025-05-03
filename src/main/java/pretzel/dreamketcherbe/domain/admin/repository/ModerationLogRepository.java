package pretzel.dreamketcherbe.domain.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.admin.entity.ModerationLog;

public interface ModerationLogRepository extends JpaRepository<ModerationLog, Long> {

}
