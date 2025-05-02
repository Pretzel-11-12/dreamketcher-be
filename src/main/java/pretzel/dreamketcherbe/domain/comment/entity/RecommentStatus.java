package pretzel.dreamketcherbe.domain.comment.entity;

public enum RecommentStatus {
    NORMAL,
    REPORTED,
    DELETED;

    /**
     * 주어진 문자열에 해당하는 RecommentStatus 열거형 상수를 반환합니다.
     *
     * @param status 변환할 상태 문자열
     * @return 해당하는 RecommentStatus 열거형 상수
     * @throws IllegalArgumentException 유효하지 않은 상태 문자열이 입력된 경우 발생
     */
    public static RecommentStatus of(String status) {
        return valueOf(status);
    }
}
