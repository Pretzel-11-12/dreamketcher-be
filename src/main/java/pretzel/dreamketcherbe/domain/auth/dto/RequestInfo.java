package pretzel.dreamketcherbe.domain.auth.dto;

public record RequestInfo(
    Long memberId,
    String ip,
    boolean isGuest
) {

    /**
     * 주어진 회원 ID로 로그인한 사용자의 RequestInfo 인스턴스를 생성합니다.
     *
     * @param memberId 회원의 고유 식별자
     * @return 로그인한 사용자를 나타내는 RequestInfo 객체
     */
    public static RequestInfo fromMember(Long memberId) {
        return new RequestInfo(memberId, null, false);
    }

    /**
     * 게스트 사용자의 요청 정보를 생성합니다.
     *
     * @param ip 게스트 사용자의 IP 주소
     * @return 게스트 사용자를 나타내는 RequestInfo 인스턴스
     */
    public static RequestInfo fromGuest(String ip) {
        return new RequestInfo(null, ip, true);
    }
}
