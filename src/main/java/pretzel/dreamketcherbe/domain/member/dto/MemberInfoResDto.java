package pretzel.dreamketcherbe.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.Role;

@JsonInclude(Include.NON_NULL)
public record MemberInfoResDto(
    String email,
    String businessEmail,
    String name,
    String nickname,
    String shortIntroduction,
    String imageUrl,
    Role role
) {

    /* 본인 전체 정보 → 모든 필드 채움 */
    public static MemberInfoResDto ofSelf(Member m) {
        return new MemberInfoResDto(
            m.getEmail(),
            m.getBusinessEmail(),
            m.getName(),
            m.getNickname(),
            m.getShortIntroduction(),
            m.getImageUrl(),
            m.getRole()
        );
    }

    /* 다른 회원 프로필(4 필드만) */
    public static MemberInfoResDto ofProfile(Member m) {
        return new MemberInfoResDto(
            null,
            m.getBusinessEmail(),
            null,
            m.getNickname(),
            m.getShortIntroduction(),
            m.getImageUrl(),
            null
        );
    }
}
