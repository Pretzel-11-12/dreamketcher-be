package pretzel.dreamketcherbe.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;

@Slf4j
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

    @Column(name = "nickname", unique = true, nullable = false)
    private String nickname;

    @Column(name = "image_uri")
    private String imageUri;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(SocialType socialType, String socialId, String email, String name,String nickName, String imageUri, Role role) {
        this.socialType = socialType;
        this.socialId = socialId;
        this.name = name;
        this.nickname = nickName;
        this.email = email;
        this.imageUri = imageUri;
        this.role = role;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public void updateProfile(String nickname) {
        this.nickname = nickname;
    }

    public void updateRole(Role role) {
        this.role = role;
    }
}
