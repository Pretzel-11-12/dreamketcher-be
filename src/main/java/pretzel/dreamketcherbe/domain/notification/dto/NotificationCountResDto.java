package pretzel.dreamketcherbe.domain.notification.dto;

import jakarta.validation.constraints.NotNull;

public record NotificationCountResDto(
    @NotNull Long count
) {

}
