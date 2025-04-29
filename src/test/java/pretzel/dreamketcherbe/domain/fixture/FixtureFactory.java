package pretzel.dreamketcherbe.domain.fixture;

import java.lang.reflect.Field;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.Role;
import pretzel.dreamketcherbe.domain.member.entity.SocialType;
import pretzel.dreamketcherbe.domain.member.entity.StorageFolder;

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

    public static StorageFolder getStorageFolder(Long id) {
        StorageFolder storageFolder = StorageFolder.builder()
            .name("폴더 이름")
            .member(getMember(1L))
            .build();

        setId(storageFolder, id);
        return storageFolder;
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

    private static void setId(StorageFolder storageFolder, Long id) {
        try {
            Field field = StorageFolder.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(storageFolder, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
