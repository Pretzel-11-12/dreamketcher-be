package pretzel.dreamketcherbe.domain.admin.entity;

public enum ActionType {
    COMMENT_HIDDEN,
    EPISODE_HIDDEN,
    USER_SUSPENDED,
    REPORT_DISMISSED,
    REPORT_RESOLVED,
    ;

    /**
     * 주어진 문자열에 해당하는 ActionType 열거형 상수를 반환합니다.
     *
     * @param actionType 변환할 액션 타입 이름
     * @return 해당하는 ActionType 상수
     * @throws IllegalArgumentException actionType이 유효한 상수 이름이 아닐 경우 발생
     */
    public static ActionType of(String actionType) {
        return valueOf(actionType);
    }
}
