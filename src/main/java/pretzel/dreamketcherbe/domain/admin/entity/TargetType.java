package pretzel.dreamketcherbe.domain.admin.entity;

public enum TargetType {
    COMMENT,
    EPISODE,
    USER,
    REPORT;

    /**
     * 주어진 문자열에 해당하는 TargetType 열거형 상수를 반환합니다.
     *
     * @param targetType 변환할 대상 타입의 문자열
     * @return 대응되는 TargetType 열거형 상수
     * @throws IllegalArgumentException targetType이 유효한 상수 이름이 아닐 경우 발생
     */
    public static TargetType of(String targetType) {
        return valueOf(targetType);
    }
}
