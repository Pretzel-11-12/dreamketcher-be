package pretzel.dreamketcherbe.domain.comment.entity;

public enum CommentStatus {
    NORMAL,
    REPORTED,
    DELETED;

    /**
     * 주어진 문자열에 해당하는 CommentStatus 열거형 상수를 반환합니다.
     *
     * @param status 변환할 상태 문자열
     * @return 해당하는 CommentStatus 열거형 상수
     * @throws IllegalArgumentException status가 유효한 상수 이름이 아닐 경우 발생
     */
    public static CommentStatus of(String status) {
        return valueOf(status);
    }
}