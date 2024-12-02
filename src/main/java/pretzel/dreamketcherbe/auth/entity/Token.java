package pretzel.dreamketcherbe.auth.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pretzel.dreamketcherbe.common.entity.BaseTimeEntity;


@Getter
@Entity
@Table(name = "refresh_token")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Token extends BaseTimeEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "member_id", nullable = false)
        private Long memberId;

        @Column(name = "token_id")
        private String tokenId;

        @Builder
        public Token(Long memberId) {
            this.tokenId = generatedTokenId();
        }

        public void renewTokenId() {
            this.tokenId = generatedTokenId();
        }

        private String generatedTokenId() {
            return java.util.UUID.randomUUID().toString();
        }
}
