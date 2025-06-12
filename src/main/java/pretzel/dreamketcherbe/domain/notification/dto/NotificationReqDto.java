package pretzel.dreamketcherbe.domain.notification.dto;

import java.util.List;

public record NotificationReqDto(
    List<NotificationItem> notifications
) {

    public record NotificationItem(
        String type,
        Long notificationId
    ) {

    }
}
