package pretzel.dreamketcherbe.domain.member.dto;

import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.Role;

public record SelfInfoResponse(
    Long id,
    String email,
    String name,
    String imageUrl,
    Role role
) {

    public static SelfInfoResponse of(Member member) {
        return new SelfInfoResponse(
            member.getId(),
            member.getEmail(),
            member.getName(),
            member.getImageUrl(),
            member.getRole()
            );
    }
}
