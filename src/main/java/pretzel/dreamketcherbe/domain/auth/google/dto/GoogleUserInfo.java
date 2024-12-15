package pretzel.dreamketcherbe.domain.auth.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.Role;
import pretzel.dreamketcherbe.domain.member.entity.SocialType;

public record GoogleUserInfo(
    @JsonProperty("sub") String socialId,
    @JsonProperty("email") String email,
    @JsonProperty("name") String name,
    @JsonProperty("picture") String imageUrl
) {
    public Member toMember(String nickname) {
        return Member.builder()
                   .socialType(SocialType.GOOGLE)
                   .socialId(socialId)
                   .email(email)
                   .name(name)
                   .nickname(nickname)
                   .imageUrl(imageUrl)
                   .role(Role.MEMBER)
                   .build();
    }
}
