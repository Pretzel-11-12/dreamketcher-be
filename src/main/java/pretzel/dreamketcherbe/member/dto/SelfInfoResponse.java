package pretzel.dreamketcherbe.member.dto;

import pretzel.dreamketcherbe.member.entity.Member;
import pretzel.dreamketcherbe.member.entity.Role;

public record SelfInfoResponse(
    Long id,
    String email,
    String name,
    String imageUri,
    Role role
) {

    public static SelfInfoResponse of(Member member) {
        return new SelfInfoResponse(
            member.getId(),
            member.getEmail(),
            member.getName(),
            member.getImageUri(),
            member.getRole()
            );
    }
}
