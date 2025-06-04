package pretzel.dreamketcherbe.domain.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretzel.dreamketcherbe.domain.notification.entity.CommentReportNotification;

public interface CommentReportNotificationRepository extends
    JpaRepository<CommentReportNotification, Long> {


}
