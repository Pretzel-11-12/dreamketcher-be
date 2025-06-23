package pretzel.dreamketcherbe.domain.notification.dto;

import java.time.LocalDateTime;

public record NotificationDto(
    String message,
    LocalDateTime createdAt
) {

}
