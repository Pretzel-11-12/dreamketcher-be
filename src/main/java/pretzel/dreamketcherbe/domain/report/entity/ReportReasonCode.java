package pretzel.dreamketcherbe.domain.report.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportReasonCode {
    INSULT("욕설/비하"),
    PORN("음란성/성적 콘텐츠"),
    VIOLENCE("폭력/선정성"),
    ILLEGAL("불법 행위 조장"),
    GAMBLING("도박 홍보"),
    SPAM("스팸/도배"),
    SCAM("사기/피싱"),
    COPYRIGHT("저작권 침해"),
    IMPERSONATION("사칭/도용"),
    OUT_OF_SCOPE("게시판 성격 무관"),
    ETC("기타");

    private final String defaultDescription;

}

