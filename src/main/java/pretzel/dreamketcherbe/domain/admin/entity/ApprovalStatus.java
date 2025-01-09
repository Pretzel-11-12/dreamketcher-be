package pretzel.dreamketcherbe.domain.admin.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ApprovalStatus {
    NOT_APPROVAL("NOT_APPROVAL", "미승인"),
    APPROVAL_DENIED("APPROVAL_DENIED", "승인 거절"),
    APPROVAL("APPROVAL", "승인");

    private String status;
    private String value;

    ApprovalStatus(String status, String value) {
        this.status = status;
        this.value = value;
    }

    public static boolean isValidStatus(String status) {
        return Arrays.stream(ApprovalStatus.values())
                .anyMatch(approvalStatus -> approvalStatus.getStatus().equals(status));
    }
}
