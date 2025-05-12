package pretzel.dreamketcherbe.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;

@Table(name = "member")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Column(name = "social_id", unique = true, nullable = false)
    private String socialId;

    @Column(unique = true)
    private String email;

    @Column(name = "business_email", unique = true)
    private String businessEmail;

    @Column(nullable = false)
    private String name;

    @Column(name = "nickname", unique = true, nullable = false)
    private String nickname;

    @Column(name = "short_introduction", length = 255)
    private String shortIntroduction;

    @Column(name = "image_uri")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;
    
    @Column(name = "suspended_until")
    private LocalDateTime suspendedUntil;
    
    @Column(name = "suspension_reason", length = 500)
    private String suspensionReason;

    /**
     * 소셜 로그인 정보를 기반으로 새로운 회원 엔티티를 생성합니다.
     *
     * @param socialType 소셜 로그인 유형
     * @param socialId 소셜 플랫폼에서 발급된 고유 식별자
     * @param email 회원의 이메일 주소
     * @param name 회원 이름
     * @param nickname 회원 닉네임
     * @param role 회원 역할
     */
    @Builder
    public Member(SocialType socialType, String socialId, String email, String name,
        String nickname, Role role) {
        this.socialType = socialType;
        this.socialId = socialId;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.status = MemberStatus.ACTIVE;
    }

    /**
     * 회원이 관리자 권한을 가지고 있는지 여부를 반환합니다.
     *
     * @return 관리자인 경우 true, 그렇지 않으면 false
     */
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }
    
    /**
     * 현재 회원이 정지 상태인지 여부를 반환합니다.
     *
     * 회원의 상태가 SUSPENDED이고, 정지 해제 시간이 없거나 아직 도래하지 않은 경우 true를 반환합니다.
     *
     * @return 회원이 현재 정지 상태이면 true, 아니면 false
     */
    public boolean isSuspended() {
        return this.status == MemberStatus.SUSPENDED && 
               (this.suspendedUntil == null || this.suspendedUntil.isAfter(LocalDateTime.now()));
    }

    /**
     * 회원의 닉네임을 변경합니다.
     *
     * @param nickname 새로 설정할 닉네임
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateBusinessEmail(String businessEmail) {
        this.businessEmail = businessEmail;
    }

    public void updateShortIntroduction(String shortIntroduction) {
        this.shortIntroduction = shortIntroduction;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * 회원의 역할(Role)을 변경한다.
     *
     * @param role 새로 설정할 역할
     */
    public void updateRole(Role role) {
        this.role = role;
    }
    
    /**
     * 회원을 정지 상태로 전환하고 정지 기간과 사유를 설정합니다.
     *
     * @param suspendedUntil 정지 해제 예정 시각
     * @param reason 정지 사유
     */
    public void suspend(LocalDateTime suspendedUntil, String reason) {
        this.status = MemberStatus.SUSPENDED;
        this.suspendedUntil = suspendedUntil;
        this.suspensionReason = reason;
    }
    
    /**
     * 회원의 상태를 활성화로 변경하고, 모든 정지 관련 정보를 초기화합니다.
     */
    public void activate() {
        this.status = MemberStatus.ACTIVE;
        this.suspendedUntil = null;
        this.suspensionReason = null;
    }
}
