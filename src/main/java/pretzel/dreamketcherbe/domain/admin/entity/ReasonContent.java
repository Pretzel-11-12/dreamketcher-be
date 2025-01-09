package pretzel.dreamketcherbe.domain.admin.entity;

import lombok.Getter;
import pretzel.dreamketcherbe.domain.admin.exception.AdminException;
import pretzel.dreamketcherbe.domain.admin.exception.AdminExceptionType;

import java.util.Arrays;

@Getter
public enum ReasonContent {

    COPYRIGHT("저작권 위반 작품 내 저작권을 침해하는 콘텐츠를 포함", "COPYRIGHT"),
    VIOLATION("공모전 규정 위반", "VIOLATION"),
    INAPPROPRIATE("부적절한 컨텐츠 폭력적이거나 선정적인 콘텐츠", "INAPPROPRIATE"),
    DISCRIMINATION("차별/갈등 조장", "DISCRIMINATION"),
    PERSONAL("작가 개인 사정", "PERSONAL"),
    PROFANITY("비속어 및 부적절한 표현", "PROFANITY");

    private final String content;
    private final String keyword;

    ReasonContent(String content, String keyword) {
        this.content = content;
        this.keyword = keyword;
    }

    public static String getContentByKeyword(String keyword) {
        for (ReasonContent reason : values()) {
            if (reason.keyword.equalsIgnoreCase(keyword)) {
                return reason.content;
            }
        }
        throw new AdminException(AdminExceptionType.REASON_NOT_FOUND);
    }

    public static boolean isValidReason(String keyword) {
        return Arrays.stream(ReasonContent.values())
            .anyMatch(reasonContent -> reasonContent.getKeyword().equals(keyword));
    }
}
