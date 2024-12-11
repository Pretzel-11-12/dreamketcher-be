package pretzel.dreamketcherbe.domain.admin.entity;

public enum ReasonContent {

    COPYRIGHT("저작권 위반 작품 내 저작권을 침해하는 콘텐츠를 포함", "Copyright"),
    VIOLATION("공모전 규정 위반", "Violation"),
    INAPPROPRIATE("부적절한 컨텐츠 폭력적이거나 선정적인 콘텐츠", "Inappropriate"),
    DISCRIMINATION("차별 / 갈등 조장", "Discrimination"),
    PERSONAL("작가 개인 사정", "Personal"),
    PROFANITY("비속어 및 부적절한 표현", "Profanity");

    private final String content;
    private final String keyword;

    ReasonContent(String content, String keyword) {
        this.content = content;
        this.keyword = keyword;
    }
}
