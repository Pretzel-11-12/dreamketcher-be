package pretzel.dreamketcherbe.domain.fixture;

import java.lang.reflect.Field;
import pretzel.dreamketcherbe.domain.member.entity.Member;
import pretzel.dreamketcherbe.domain.member.entity.Role;
import pretzel.dreamketcherbe.domain.member.entity.SocialType;
import pretzel.dreamketcherbe.domain.member.entity.StorageFolder;
import pretzel.dreamketcherbe.domain.webtoon.entity.Genre;
import pretzel.dreamketcherbe.domain.webtoon.entity.Webtoon;

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

    public static Webtoon getWebtoon(Long id, Member member) {
        Webtoon webtoon = Webtoon.builder()
            .title("테스트 웹툰")
            .thumbnail("https://example.com/thumbnail.jpg")
            .story("테스트 스토리입니다.")
            .status("PRE_SERIES")
            .member(member)
            .genre(getGenre(1L))
            .build();

        setId(webtoon, id);
        return webtoon;
    }

    public static Genre getGenre(Long id) {
        return Genre.builder()
            .id(id)
            .name("장르")
            .build();
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

    private static void setId(Webtoon webtoon, Long id) {
        try {
            Field field = Webtoon.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(webtoon, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
