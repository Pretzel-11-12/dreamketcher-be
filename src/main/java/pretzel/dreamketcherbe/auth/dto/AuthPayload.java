package pretzel.dreamketcherbe.auth.dto;

import pretzel.dreamketcherbe.member.entity.Role;

public record AuthPayload(
        Long memberId,
        Role role
) {
}
