package pretzel.dreamketcherbe.domain.auth.google.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.Role;
import pretzel.dreamketcherbe.domain.member.entity.SocialType;

public record GoogleUserInfo(
    @JsonProperty("sub") String socialId,
    @JsonProperty("email") String email,
    @JsonProperty("name") String name,
    @JsonProperty("picture") String imageUri
) {
    public Member toMember(String nickName) {
        return Member.builder()
                   .socialType(SocialType.GOOGLE)
                   .socialId(socialId)
                   .email(email)
                   .name(name)
                   .nickName(nickName)
                   .imageUri(imageUri)
                   .role(Role.MEMBER)
                   .build();
    }
}
