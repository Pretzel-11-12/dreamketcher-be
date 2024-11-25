package pretzel.dreamketcherbe.member.entity;

import jakarta.persistence.*;
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

    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(SocialType socialType, String socialId, String email, String name, Role role) {
        this.socialType = socialType;
        this.socialId = socialId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateRole(Role role) {
        this.role = role;
    }
}
