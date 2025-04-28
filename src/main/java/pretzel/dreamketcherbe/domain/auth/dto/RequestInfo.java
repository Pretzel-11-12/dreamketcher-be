package pretzel.dreamketcherbe.domain.auth.dto;

public record RequestInfo(
    Long memberId,
    String ip,
    boolean isGuest
) {

    public static RequestInfo fromMember(Long memberId) {
        return new RequestInfo(memberId, null, false);
    }

    public static RequestInfo fromGuest(String ip) {
        return new RequestInfo(null, ip, true);
    }
}
