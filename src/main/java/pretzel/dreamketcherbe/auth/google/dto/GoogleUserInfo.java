package pretzel.dreamketcherbe.auth.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pretzel.dreamketcherbe.member.entity.Member;
import pretzel.dreamketcherbe.member.entity.Role;
import pretzel.dreamketcherbe.member.entity.SocialType;

public record GoogleUserInfo(
    @JsonProperty("sub") String socialId,
    @JsonProperty("email") String email,
    @JsonProperty("name") String name,
    @JsonProperty("picture") String imageUri
) {
    public Member toMember() {
        return Member.builder()
                   .socialType(SocialType.GOOGLE)
                   .socialId(socialId)
                   .email(email)
                   .name(name)
                   .imageUri(imageUri)
                   .role(Role.MEMBER)
                   .build();
    }
}
