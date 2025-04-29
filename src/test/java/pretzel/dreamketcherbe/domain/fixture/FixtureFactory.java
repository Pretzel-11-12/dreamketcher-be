package pretzel.dreamketcherbe.domain.fixture;

import java.lang.reflect.Field;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.Role;
import pretzel.dreamketcherbe.domain.member.entity.SocialType;

public class FixtureFactory {

    public static Member getMember(Long id) {
        Member member = Member.builder()
            .socialType(SocialType.GOOGLE)
            .socialId("socialId123")
            .email("testuser@example.com")
            .name("테스트 유저")
            .nickname("testuser")
            .role(Role.MEMBER)
            .build();

        setId(member, id);
        return member;
    }

    private static void setId(Member member, Long id) {
        try {
            Field field = Member.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(member, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
