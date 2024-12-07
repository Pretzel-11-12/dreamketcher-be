package pretzel.dreamketcherbe.domain.auth.utils;

import java.util.List;
import java.util.Random;

public class NicknameGenerator {

    private static final List<String> ADJECTIVES = List.of(
        "행복한", "슬픈", "화난", "기쁜", "무서운", "놀라운", "지루한", "즐거운", "힘든", "쉬운",
        "배부른", "좋은", "나쁜", "착한", "부끄러운", "숙쓰러운", "심술난", "귀여운", "멋진", "평범한",
        "특별한", "아름다운", "못생긴", "예쁜", "잘생긴", "힘찬", "부드러운", "단단한", "부서진", "빠른",
        "배고픈", "시끄러운", "조용한", "시샘하는", "더워하는", "무덤덤한", "무뚝뚝한");

    private static final List<String> BIRDS = List.of(
        "참새", "비둘기", "까마귀", "독수리", "부엉이", "올빼미", "앵무새", "독수리", "타조", "펭귄",
        "회색앵무", "후투티", "사랑앵무", "박새", "딱새", "뱁새", "동박새", "가마우지", "두루미");

    private static final Random random = new Random();

    public static String generate() {
        return ADJECTIVES.get(random.nextInt(ADJECTIVES.size())) + " " + BIRDS.get(random.nextInt(BIRDS.size()));
    }

    public static String generateWithRandomSuffix() {
        return generate() + ((int) (Math.random() * 9000) + 1000);
    }
}
